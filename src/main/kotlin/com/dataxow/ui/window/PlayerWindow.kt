package com.dataxow.ui.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    text: String,
    mediaPlayer: EmbeddedMediaPlayer,
    videoSurface: CallbackVideoSurface,
    adapter: RenderCallbackAdapter,
    fontPoppinsBold: FontFamily,
    onCloseRequest: () -> Unit
) {
    Window(
        onCloseRequest = { onCloseRequest() },
        title = "Player",
        undecorated = true,
        state = windowState,
        alwaysOnTop = true
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            imagePath?.let {
                Image(
                    bitmap = Image.makeFromEncoded(File(it).readBytes()).toComposeImageBitmap(),
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
                        join = StrokeJoin.Round
                    )
                ),
                maxFontSize = 200.sp,
                modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
            )
        }
    }
}
