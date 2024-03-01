package com.dataxow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.dataxow.helper.FileHelper
import com.dataxow.helper.ImageHelper
import com.dataxow.helper.SystemHelper
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import com.dataxow.renderer.RenderCallbackAdapter
import com.dataxow.ui.components.autoSizeText
import com.dataxow.ui.components.ipSelector
import com.dataxow.ui.components.videoPlayer
import com.dataxow.ui.helper.monitorWatcher
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
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import org.jetbrains.skia.Image as SkiaImage

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

    var text by remember { mutableStateOf("Your text here") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var videoPath by remember { mutableStateOf<String?>(null) }
    var secondWindowOpen by remember { mutableStateOf(false) }

    val ips = getLocalIPAddresses()

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
        Window(onCloseRequest = ::exitApplication, title = "Control Window") {
            Scaffold {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Text to display") }
                        )
                        Button(onClick = { secondWindowOpen = true }) {
                            Text("Update Text")
                        }
                    }
                    Button(onClick = {
                        val file = FileHelper.selectFile("Select Image", "image/*")
                        imagePath = file?.absolutePath
                        videoPath = null // Reset video path to stop video if playing
                        secondWindowOpen = true
                    }) {
                        Text("Select Image")
                    }
                    Button(onClick = {
                        val file = FileHelper.selectFile("Select Video", "video/*")
                        videoPath = file?.absolutePath
                        imagePath = null // Reset image path to remove image if displayed
                        secondWindowOpen = true
                    }) {
                        Text("Select Video")
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ipSelector(items = ips, selectedItem = serverHost, onItemSelected = { serverHost = it })
                            TextField(value = serverPort, onValueChange = { serverPort = it }, label = { Text("Port") })
                        }
                        Row {
                            Button(onClick = {
                                if (!serverStatus) {
                                    startServer(serverHost, serverPort.toInt())
                                    qrCodeBitmap =
                                        ImageHelper.generateQRCode(
                                            "http://$serverHost:$serverPort/rcontrol/?api_url=http://$serverHost:$serverPort",
                                        )
                                    serverStatus = true
                                } else {
                                    stopServer()
                                    qrCodeBitmap = null
                                    serverStatus = false
                                }
                            }) {
                                Text(if (!serverStatus) "Start Server" else "Stop Server")
                            }
                        }
                        qrCodeBitmap?.let { image ->
                            Image(bitmap = image, contentDescription = "QR Code")
                        }
                    }
                }
            }
        }
    }

    monitorWatcher { isMultiMonitor, screenDevice ->
        windowState = SystemHelper.getMonitorState(isMultiMonitor, screenDevice)
    }

    // Second window logic
    if (secondWindowOpen) {
        Window(
            onCloseRequest = { secondWindowOpen = false },
            title = "Player",
            undecorated = false,
            state = windowState
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                imagePath?.let {
                    Image(
                        bitmap = SkiaImage.makeFromEncoded(File(it).readBytes()).toComposeImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                if (videoPath != null) {
                    mediaPlayer.videoSurface().set(videoSurface)
                    mediaPlayer.media().play(videoPath)

                    videoPlayer(
                        adapter,
                        Modifier.fillMaxSize()
                    )
                }
                autoSizeText(
                    text = text,
                    style = TextStyle(
                        fontFamily = fontPoppinsBold,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        background = Color.Transparent,
                        textAlign = TextAlign.Center,
                    ),
                    maxFontSize = 200.sp,
                    modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
                )
                autoSizeText(
                    text = text,
                    style = TextStyle(
                        fontFamily = fontPoppinsBold,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        background = Color.Transparent,
                        textAlign = TextAlign.Center,
                        drawStyle = Stroke(
                            miter = 10f,
                            width = 5f,
                            join = StrokeJoin.Round,

                            )
                    ),
                    maxFontSize = 200.sp,
                    modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
                )
            }
        }
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

fun getLocalIPAddresses(): List<String> {
    return NetworkInterface.getNetworkInterfaces().asSequence().flatMap { ni ->
        ni.inetAddresses.asSequence().filter { it.isSiteLocalAddress && it is InetAddress }.map { it.hostAddress }
    }.toList()
}
