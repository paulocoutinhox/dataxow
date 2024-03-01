package com.dataxow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
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
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.nio.ByteBuffer
import org.jetbrains.skia.Image as SkiaImage

val adapter = RenderCallbackAdapter(800, 600)
val videoSurface = CallbackVideoSurface(adapter, adapter, true, VideoSurfaceAdapters.getVideoSurfaceAdapter())
val mediaPlayerFactory = MediaPlayerFactory()
val mediaPlayer: EmbeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer()

fun main() = application {
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
                    mediaPlayer.videoSurface().set(videoSurface)
                    mediaPlayer.media().play(videoPath)

                    videoPlayerImpl(
                        adapter,
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
    adapter: RenderCallbackAdapter,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        videoFrame(adapter.imageBitmap)
    }
}

@Composable
fun videoFrame(imageBitmap: ImageBitmap?) {
    imageBitmap?.let {
        Image(
            bitmap = it,
            contentDescription = "Video Frame",
            modifier = Modifier.fillMaxSize()
        )
    }
}


class RenderCallbackAdapter(private val width: Int, private val height: Int) : BufferFormatCallback, RenderCallback {
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
        return RV32BufferFormat(width, height)
    }

    override fun allocatedBuffers(buffers: Array<out ByteBuffer>) {
        // this is managed by vlcj
    }

    override fun display(mediaPlayer: MediaPlayer?, nativeBuffers: Array<ByteBuffer>, bufferFormat: BufferFormat) {
        val fixedImage = false

        if (fixedImage) {
            val testImage = File("/Users/paulo/Downloads/pexels.webp")
            val bitmap = SkiaImage.makeFromEncoded(testImage.readBytes())
            imageBitmap = bitmap.toComposeImageBitmap()
        } else {
            val buffer = nativeBuffers[0]
            buffer.rewind()

            val size = bufferFormat.width * bufferFormat.height * 4

            if (buffer.isDirect) {
                val byteArray = ByteArray(size)
                buffer.get(byteArray)
                updateImageBitmap(byteArray, bufferFormat.width, bufferFormat.height)
            } else {
                updateImageBitmap(buffer.array(), bufferFormat.width, bufferFormat.height)
            }
        }
    }

    private fun updateImageBitmap(pixelData: ByteArray, width: Int, height: Int) {
        val bitmap = Bitmap().apply {
            allocPixels(ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL))
            installPixels(imageInfo, pixelData, (width * 4))
        }

        imageBitmap = bitmap.asComposeImageBitmap()
    }
}

fun selectFile(title: String, fileType: String): File? {
    val fileDialog = FileDialog(Frame(), title, FileDialog.LOAD)
    fileDialog.isVisible = true
    return fileDialog.files.firstOrNull()
}
