package com.dataxow.helper

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.Desktop
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.net.URI
import java.net.URISyntaxException
import java.net.URL


object SystemHelper {
    fun getMonitorState(isMultiMonitor: Boolean, device: GraphicsDevice?): WindowState {
        if (isMultiMonitor) {
            device?.let {
                val configuration = it.defaultConfiguration
                val bounds = configuration.bounds

                return WindowState(
                    size = DpSize(bounds.width.dp, bounds.height.dp),
                    position = WindowPosition.Absolute(x = bounds.x.dp, y = bounds.y.dp)
                )
            }
        }

        val devices = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        val primaryMonitorBounds = devices[0].defaultConfiguration.bounds
        val windowWidth = 320.dp.value.toInt()
        val windowHeight = 240.dp.value.toInt()
        val positionX = (primaryMonitorBounds.width - windowWidth).dp
        val positionY = (primaryMonitorBounds.height - windowHeight).dp

        return WindowState(
            size = DpSize(320.dp, 240.dp),
            position = WindowPosition.Absolute(x = positionX, y = positionY)
        )
    }

    fun openWebPage(uri: URI?): Boolean {
        val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return false
    }

    fun openWebPage(url: URL): Boolean {
        try {
            return openWebPage(url.toURI())
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        return false
    }

    fun openWebPage(url: String): Boolean {
        try {
            return openWebPage(URI(url))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }

        return false
    }
}
