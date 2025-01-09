package com.dataxow.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import coil3.compose.AsyncImage
import com.dataxow.app.AppData
import com.dataxow.renderer.RenderCallbackAdapter
import com.dataxow.ui.components.autoSizeText
import com.dataxow.ui.components.videoPlayer
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
                if (imagePath.startsWith("http")) {
                    AsyncImage(
                        model = imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = File(imagePath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
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
                fontFamily = textFontFamily,
                style = TextStyle(
                    color = Color.White,
                    background = Color.Transparent,
                    textAlign = TextAlign.Center,
                ),
                outlineColor = Color.Black,
                outlineWidth = 1.dp,
                maxFontSize = 200.sp,
                modifier = Modifier.align(Alignment.Center).wrapContentSize(Alignment.Center)
            )
        }
    }
}
