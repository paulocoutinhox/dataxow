package com.dataxow

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.dataxow.app.AppData
import com.dataxow.app.ConfigManager
import com.dataxow.app.configureRoutes
import com.dataxow.helper.NetHelper
import com.dataxow.helper.SystemHelper
import com.dataxow.ui.helper.monitorWatcher
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
import kotlinx.serialization.json.Json

fun main() = application {
    var windowState by remember { mutableStateOf(WindowState()) }
    var forceUpdateWindowState by remember { mutableStateOf(false) }

    var projectPath by remember { mutableStateOf(AppData.config.project) }

    var text by remember { mutableStateOf("DataXow\nSample\nText") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var videoPath by remember { mutableStateOf<String?>(null) }
    var playerWindowOpen by remember { mutableStateOf(false) }

    val ips = NetHelper.getLocalIPAddresses()

    var serverHost by remember { mutableStateOf(if (ips.isNotEmpty()) ips.first() else "") }
    var serverPort by remember { mutableStateOf(AppData.config.serverPort) }
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var serverStatus by remember { mutableStateOf(false) }

    AppData.onTextUpdate = { newText ->
        text = newText
        playerWindowOpen = true
    }

    AppData.onImageUpdate = { newImage ->
        imagePath = newImage
        videoPath = null
        playerWindowOpen = true
    }

    AppData.onVideoUpdate = { newVideo ->
        videoPath = newVideo
        imagePath = null
        playerWindowOpen = true
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
            },
            forceUpdateWindowState = forceUpdateWindowState,
            setForceUpdateWindowState = { forceUpdateWindowState = it },
            text = text,
            setText = { text = it },
            imagePath = imagePath,
            setImagePath = { imagePath = it },
            videoPath = videoPath,
            setVideoPath = { videoPath = it },
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
            stopServer = ::stopServer
        )
    }

    // Player window
    monitorWatcher(forceUpdateWindowState) { isMultiMonitor, screenDevice ->
        windowState = SystemHelper.getMonitorState(isMultiMonitor, screenDevice)
    }

    if (playerWindowOpen) {
        playerWindow(
            windowState = windowState,
            imagePath = imagePath,
            videoPath = videoPath,
            text = text,
            mediaPlayer = AppData.mediaPlayer,
            videoSurface = AppData.videoSurface,
            adapter = AppData.adapter,
            fontPoppinsBold = AppData.fontPoppinsBold,
            onCloseRequest = {
                playerWindowOpen = false
            }
        )
    }

    // Exit
    Runtime.getRuntime().addShutdownHook(Thread {
        ConfigManager.saveConfig(AppData.config)
    })
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
