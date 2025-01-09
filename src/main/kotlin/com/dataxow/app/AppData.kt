package com.dataxow.app

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import com.dataxow.model.FileListItem
import com.dataxow.renderer.RenderCallbackAdapter
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.flow.MutableStateFlow
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters
import java.awt.GraphicsDevice

object AppData {
    private val mediaPlayerFactory = MediaPlayerFactory()

    var isMultiScreen = false
        @Synchronized
        get
        @Synchronized
        set

    var playerScreenDevice: GraphicsDevice? = null
        @Synchronized
        get
        @Synchronized
        set

    val config = ConfigManager.loadConfig()

    var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

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

    var onLoading: ((Boolean) -> Unit)? = null
    var onTextUpdate: ((String) -> Unit)? = null
    var onImageUpdate: ((String) -> Unit)? = null
    var onVideoUpdate: ((String) -> Unit)? = null
    var onShowPlayer: (() -> Unit)? = null
    var onClosePlayer: (() -> Unit)? = null
    var onClearPlayer: (() -> Unit)? = null

    var onSystemScreenUpdates: ((isMultiScreen: Boolean, device: GraphicsDevice?) -> Unit)? = null

    val colorPalette = lightColors(
        primary = Color(0xFF3F51B5),
    )

    var liveText = MutableStateFlow("DataXow\nSample\nText")
    var liveTextIndex = MutableStateFlow(-1)

    var imageList = MutableStateFlow<ArrayList<FileListItem>>(arrayListOf())
    var videoList = MutableStateFlow<ArrayList<FileListItem>>(arrayListOf())
    var textList = MutableStateFlow<ArrayList<FileListItem>>(arrayListOf())
    var previewTextList = MutableStateFlow<ArrayList<String>>(arrayListOf())
}
