package com.dataxow

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.dataxow.app.AppData
import com.dataxow.app.ConfigManager
import com.dataxow.app.configureRoutes
import com.dataxow.helper.ImageListHelper
import com.dataxow.helper.NetHelper
import com.dataxow.helper.SystemHelper
import com.dataxow.helper.VideoListHelper
import com.dataxow.ui.helper.systemScreenWatcher
import com.dataxow.ui.window.mainWindow
import com.dataxow.ui.window.playerWindow
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.io.File

fun main() = application {
    var windowState by remember { mutableStateOf(WindowState()) }

    var projectPath by remember { mutableStateOf(AppData.config.project) }

    var imagePath by remember { mutableStateOf<String?>(null) }
    var videoPath by remember { mutableStateOf<String?>(null) }
    var playerWindowOpen by remember { mutableStateOf(false) }

    val ips = NetHelper.getLocalIPAddresses()

    var serverHost by remember { mutableStateOf(if (ips.isNotEmpty()) ips.first() else "") }
    var serverPort by remember { mutableStateOf(AppData.config.serverPort) }
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var serverStatus by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    AppData.onTextUpdate = { newText ->
        AppData.liveText.value = newText
        playerWindowOpen = true
    }

    AppData.onImageUpdate = { newImage ->
        imagePath = newImage
        AppData.mediaPlayer.controls().stop()
        videoPath = null
        playerWindowOpen = true
    }

    AppData.onVideoUpdate = { newVideo ->
        videoPath = newVideo
        imagePath = null
        playerWindowOpen = true
    }

    AppData.onSystemScreenUpdates = { isMultiScreen, screenDevice ->
        windowState = SystemHelper.getMonitorState(isMultiScreen, screenDevice)
    }

    AppData.onLoading = { loading ->
        isLoading = loading
    }

    AppData.onShowPlayer = {
        playerWindowOpen = true
    }

    AppData.onClosePlayer = {
        AppData.mediaPlayer.controls().stop()
        imagePath = null
        videoPath = null
        playerWindowOpen = false
    }

    AppData.onClearPlayer = {
        AppData.mediaPlayer.controls().stop()
        imagePath = null
        videoPath = null
        AppData.liveText.value = ""
    }

    AppData.mediaPlayer.controls().repeat = true

    // Main window
    MaterialTheme(
        colors = AppData.colorPalette
    ) {
        mainWindow(
            applicationScope = this,
            projectPath = projectPath,
            setProjectPath = {
                projectPath = it
                AppData.config.project = it

                reload()
            },
            isLoading = isLoading,
            setForceUpdateWindowState = {
                AppData.onSystemScreenUpdates?.invoke(AppData.isMultiScreen, AppData.playerScreenDevice)
            },
            imagePath = imagePath,
            setImagePath = {
                videoPath = null
                imagePath = it
            },
            videoPath = videoPath,
            setVideoPath = {
                imagePath = null
                videoPath = it
            },
            playerWindowOpen = playerWindowOpen,
            setPlayerWindowOpen = { playerWindowOpen = it },
            ips = NetHelper.getLocalIPAddresses(),
            serverHost = serverHost,
            setServerHost = { serverHost = it },
            serverPort = serverPort,
            setServerPort = {
                serverPort = it
                AppData.config.serverPort = it
            },
            qrCodeBitmap = qrCodeBitmap,
            setQrCodeBitmap = { qrCodeBitmap = it },
            serverStatus = serverStatus,
            setServerStatus = { serverStatus = it },
            startServer = ::startServer,
            stopServer = ::stopServer,
            reload = { reload() },
        )
    }

    // System Screen Watcher
    systemScreenWatcher { isMultiScreen, screenDevice ->
        AppData.onSystemScreenUpdates?.invoke(isMultiScreen, screenDevice)
    }

    // Player window
    if (playerWindowOpen) {
        playerWindow(
            windowState = windowState,
            imagePath = imagePath,
            videoPath = videoPath,
            mediaPlayer = AppData.mediaPlayer,
            videoSurface = AppData.videoSurface,
            adapter = AppData.adapter,
            textFontFamily = AppData.fontPoppinsBold,
            onCloseRequest = {
                playerWindowOpen = false
            }
        )
    }

    // Exit
    Runtime.getRuntime().addShutdownHook(Thread {
        ConfigManager.saveConfig(AppData.config)
    })

    // Initial Data
    LaunchedEffect(key1 = "InitialData") {
        reload()
    }
}

fun startServer(host: String, port: Int) {
    AppData.server = embeddedServer(Netty, host = host, port = port) {
        install(CORS) {
            anyHost()
            allowCredentials = true
            allowNonSimpleContentTypes = true
            allowHeader(HttpHeaders.AccessControlAllowHeaders)
            allowHeader(HttpHeaders.ContentType)
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Post)
            exposeHeader(HttpHeaders.AccessControlAllowOrigin)
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        routing {
            configureRoutes()
        }
    }.apply {
        start(wait = false)
    }
}

fun stopServer() {
    AppData.server?.stop(0, 0)
}

@OptIn(DelicateCoroutinesApi::class)
fun reload() {
    GlobalScope.launch {
        AppData.onLoading?.invoke(true)

        reloadImages()
        reloadVideos()

        AppData.onLoading?.invoke(false)
    }
}

suspend fun reloadImages() {
    AppData.onLoading?.invoke(true)

    val images = withContext(Dispatchers.IO) {
        ImageListHelper.loadImagesFromPath(
            File(AppData.config.project, "images").absolutePath,
        )
    }

    AppData.imageList.value = ArrayList(images)
}

suspend fun reloadVideos() {
    AppData.onLoading?.invoke(true)

    val images = withContext(Dispatchers.IO) {
        VideoListHelper.loadVideosFromPath(
            File(AppData.config.project, "videos").absolutePath,
        )
    }

    AppData.videoList.value = ArrayList(images)
}
