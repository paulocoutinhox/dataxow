package com.dataxow.ui.helper

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

@Composable
fun monitorWatcher(forceUpdate: Boolean, onMonitorChange: (isMultiMonitor: Boolean, device: GraphicsDevice?) -> Unit) {
    var lastMonitorCount by remember { mutableStateOf(-1) }
    var lastForceUpdate by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "monitorWatcher", key2 = forceUpdate) {
        val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screenDevices = graphicsEnvironment.screenDevices
        val currentMonitorCount = screenDevices.size
        val isMultiMonitor = currentMonitorCount > 1
        val screenDevice = if (isMultiMonitor) screenDevices[1] else screenDevices.getOrNull(0)

        if (lastMonitorCount != currentMonitorCount || lastForceUpdate != forceUpdate) {
            onMonitorChange(isMultiMonitor, screenDevice)
            lastMonitorCount = currentMonitorCount
            lastForceUpdate = forceUpdate
        }

        delay(1000)
    }
}
