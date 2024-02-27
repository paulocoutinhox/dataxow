import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.util.*
import org.jetbrains.skia.Image as SkiaImage

fun main() = application {
    System.setProperty("compose.interop.blending", "true")

    var text by remember { mutableStateOf("Your text here") }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var videoPath by remember { mutableStateOf<String?>(null) }
    var secondWindowOpen by remember { mutableStateOf(false) }

    // Main window
    Window(onCloseRequest = ::exitApplication, title = "Control Window") {
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
                val file = selectFile("Select Image", "image/*")
                imagePath = file?.absolutePath
                videoPath = null // Reset video path to stop video if playing
                secondWindowOpen = true
            }) {
                Text("Select Image")
            }
            Button(onClick = {
                val file = selectFile("Select Video", "video/*")
                videoPath = file?.absolutePath
                imagePath = null // Reset image path to remove image if displayed
                secondWindowOpen = true
            }) {
                Text("Select Video")
            }
        }
    }

    // Second window logic
    if (secondWindowOpen) {
        Window(
            onCloseRequest = { secondWindowOpen = false },
            title = "Display Window",
            state = WindowState(width = 800.dp, height = 600.dp, position = WindowPosition.Aligned(Alignment.Center))
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
                    videoPlayerImpl(
                        videoPath!!,
                        Modifier.fillMaxSize()
                            .aspectRatio(16f / 9f, matchHeightConstraintsFirst = true)
                            .align(Alignment.Center)
                    )
                }
                Text(
                    text = text,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        background = Color.Transparent,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = text,
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        background = Color.Transparent,
                        textAlign = TextAlign.Center,
                        drawStyle = Stroke(
                            miter = 10f,
                            width = 5f,
                            join = StrokeJoin.Round,

                            )
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun videoPlayerImpl(
    url: String,
    modifier: Modifier,
) {
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }

    val factory = remember { { mediaPlayerComponent } }

    LaunchedEffect(url) { mediaPlayer.media().play(url) }
    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }
    SwingPanel(
        factory = factory,
        background = Color.Transparent,
        modifier = modifier,
        update = {

        }
    )
}

private fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}


private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}

fun selectFile(title: String, fileType: String): File? {
    val fileDialog = FileDialog(Frame(), title, FileDialog.LOAD)
    fileDialog.isVisible = true
    return fileDialog.files.firstOrNull()
}
