package com.dataxow.renderer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallbackAdapter
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat
import java.io.File
import java.nio.ByteBuffer

class RenderCallbackAdapter : BufferFormatCallbackAdapter(), RenderCallback {
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)
        private set

    override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
        return RV32BufferFormat(sourceWidth, sourceHeight)
    }

    override fun display(
        p0: MediaPlayer,
        nativeBuffers: Array<ByteBuffer>,
        bufferFormat: BufferFormat,
        p3: Int,
        p4: Int
    ) {
        val fixedImage = false

        if (fixedImage) {
            val testImage = File("/Users/paulo/Downloads/pexels.webp")
            val bitmap = Image.makeFromEncoded(testImage.readBytes())
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

    override fun lock(mediaPlayer: MediaPlayer?) {
        // ignore
    }

    override fun unlock(p0: MediaPlayer?) {
        //
    }
}
