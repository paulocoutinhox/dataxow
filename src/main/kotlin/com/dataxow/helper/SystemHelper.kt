package com.dataxow.helper

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

class SystemHelper {
    companion object {
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
            val windowHeight = 160.dp.value.toInt()
            val positionX = (primaryMonitorBounds.width - windowWidth).dp
            val positionY = (primaryMonitorBounds.height - windowHeight).dp

            return WindowState(
                size = DpSize(320.dp, 160.dp),
                position = WindowPosition.Absolute(x = positionX, y = positionY)
            )
        }
    }
}
