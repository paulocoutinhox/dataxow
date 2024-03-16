package com.dataxow.ui.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.dataxow.app.AppData
import com.dataxow.renderer.RenderCallbackAdapter
import com.dataxow.ui.components.autoSizeText
import com.dataxow.ui.components.videoPlayer
import org.jetbrains.skia.Image
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import java.io.File

@Composable
fun playerWindow(
    windowState: WindowState,
    imagePath: String?,
    videoPath: String?,
    mediaPlayer: EmbeddedMediaPlayer,
    videoSurface: CallbackVideoSurface,
    adapter: RenderCallbackAdapter,
    textFontFamily: FontFamily,
    onCloseRequest: () -> Unit
) {
    val liveText = AppData.liveText.collectAsState().value

    Window(
        onCloseRequest = { onCloseRequest() },
        title = "Player",
        undecorated = true,
        state = windowState,
        alwaysOnTop = true,
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (imagePath != null) {
                var bitmap: ImageBitmap? = null

                try {
                    bitmap = Image.makeFromEncoded(File(imagePath).readBytes()).toComposeImageBitmap()
                } catch (e: Exception) {
                    // error when load image
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    BasicText("Error!")
                }
            }
            if (videoPath != null) {
                LaunchedEffect(videoPath) {
                    mediaPlayer.videoSurface().set(videoSurface)
                    mediaPlayer.media().play(videoPath)
                }

                videoPlayer(
                    adapter,
                    Modifier.fillMaxSize()
                )
            }
            autoSizeText(
                text = liveText,
                style = TextStyle(
                    fontFamily = textFontFamily,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    background = Color.Transparent,
                    textAlign = TextAlign.Center,
                ),
                maxFontSize = 200.sp,
                modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
            )
            autoSizeText(
                text = liveText,
                style = TextStyle(
                    fontFamily = textFontFamily,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    background = Color.Transparent,
                    textAlign = TextAlign.Center,
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 5f,
                        join = StrokeJoin.Round
                    )
                ),
                maxFontSize = 200.sp,
                modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
            )
        }
    }
}
