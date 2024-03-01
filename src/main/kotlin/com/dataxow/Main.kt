package com.dataxow

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.dataxow.helper.NetHelper
import com.dataxow.helper.SystemHelper
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import com.dataxow.renderer.RenderCallbackAdapter
import com.dataxow.ui.helper.monitorWatcher
import com.dataxow.ui.window.mainWindow
import com.dataxow.ui.window.playerWindow
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters

var server: NettyApplicationEngine? = null

val adapter = RenderCallbackAdapter()
val videoSurface = CallbackVideoSurface(adapter, adapter, true, VideoSurfaceAdapters.getVideoSurfaceAdapter())
val mediaPlayerFactory = MediaPlayerFactory()
val mediaPlayer: EmbeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer()

val fontPoppinsBold = FontFamily(
    Font(
        resource = "fonts/Poppins-Bold.ttf",
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)

var onTextUpdate: ((String) -> Unit)? = null

val colorPalette = lightColors(
    primary = Color(0xFF0d6efd),
)

fun main() = application {
    var windowState by remember { mutableStateOf(WindowState()) }

    var text by remember { mutableStateOf("DataXow Demo Text") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var videoPath by remember { mutableStateOf<String?>(null) }
    var playerWindowOpen by remember { mutableStateOf(false) }

    val ips = NetHelper.getLocalIPAddresses()

    var serverHost by remember { mutableStateOf(if (ips.isNotEmpty()) ips.first() else "") }
    var serverPort by remember { mutableStateOf("8080") }
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var serverStatus by remember { mutableStateOf(false) }

    onTextUpdate = { newText ->
        text = newText
    }

    mediaPlayer.controls().repeat = true

    // Main window
    MaterialTheme(
        colors = colorPalette
    ) {
        mainWindow(
            applicationScope = this,
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
            setServerPort = { serverPort = it },
            qrCodeBitmap = qrCodeBitmap,
            setQrCodeBitmap = { qrCodeBitmap = it },
            serverStatus = serverStatus,
            setServerStatus = { serverStatus = it },
            startServer = ::startServer,
            stopServer = ::stopServer
        )
    }

    // Player window
    monitorWatcher { isMultiMonitor, screenDevice ->
        windowState = SystemHelper.getMonitorState(isMultiMonitor, screenDevice)
    }

    if (playerWindowOpen) {
        playerWindow(
            windowState = windowState,
            imagePath = imagePath,
            videoPath = videoPath,
            text = text,
            mediaPlayer = mediaPlayer,
            videoSurface = videoSurface, adapter = adapter,
            fontPoppinsBold = fontPoppinsBold,
            onCloseRequest = {
                playerWindowOpen = false
            }
        )
    }
}

fun startServer(host: String, port: Int) {
    server = embeddedServer(Netty, host = host, port = port) {
        install(CORS) {
            anyHost()
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        routing {
            staticResources("/", "web")
            post("/module/call") {
                val requestData = call.receive<RequestData>()

                if (requestData.func == "modules.player.update_data") {
                    val type = requestData.params?.get("type") ?: return@post

                    if (type == "text") {
                        val newText = requestData.params["text"]

                        if (newText != null) {
                            onTextUpdate?.invoke(newText)
                        }
                    }
                }

                call.respond(
                    ResponseData(
                        true, data = mapOf(
                            "func" to requestData.func.toString(),
                            "params" to requestData.params.toString(),
                        )
                    )
                )
            }
        }
    }.apply {
        start(wait = false)
    }
}

fun stopServer() {
    server?.stop(0, 0)
}
