package com.dataxow.app

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import com.dataxow.renderer.RenderCallbackAdapter
import io.ktor.server.netty.*
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters

object AppData {
    private val mediaPlayerFactory = MediaPlayerFactory()

    val config = ConfigManager.loadConfig()

    var server: NettyApplicationEngine? = null

    val adapter = RenderCallbackAdapter()
    val videoSurface = CallbackVideoSurface(adapter, adapter, true, VideoSurfaceAdapters.getVideoSurfaceAdapter())
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
}
