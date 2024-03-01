package com.dataxow.ui.helper

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

@Composable
fun monitorWatcher(onMonitorChange: (isMultiMonitor: Boolean, device: GraphicsDevice?) -> Unit) {
    var lastMonitorCount by remember { mutableStateOf(-1) }

    LaunchedEffect(key1 = "monitorWatcher") {
        while (true) {
            val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val screenDevices = graphicsEnvironment.screenDevices
            val currentMonitorCount = screenDevices.size

            val isMultiMonitor = currentMonitorCount > 1
            val screenDevice = if (isMultiMonitor) screenDevices[1] else screenDevices.getOrNull(0)

            if (lastMonitorCount != currentMonitorCount) {
                onMonitorChange(isMultiMonitor, screenDevice)
                lastMonitorCount = currentMonitorCount
            }

            delay(1000)
        }
    }
}
