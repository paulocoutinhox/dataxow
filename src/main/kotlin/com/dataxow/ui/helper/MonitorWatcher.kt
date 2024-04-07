package com.dataxow.ui.helper

import androidx.compose.runtime.*
import com.dataxow.app.AppData
import kotlinx.coroutines.delay
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment

@Composable
fun systemScreenWatcher(onScreenChange: (isMultiScreen: Boolean, device: GraphicsDevice?) -> Unit) {
    var lastScreenCount by remember { mutableStateOf(-1) }

    LaunchedEffect(key1 = "SystemScreenWatcher") {
        while (true) {
            val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
            val screenDevices = graphicsEnvironment.screenDevices
            val currentScreenCount = screenDevices.size
            val isMultiScreen = currentScreenCount > 1
            val screenDevice = if (isMultiScreen) screenDevices[1] else screenDevices.getOrNull(0)

            if (lastScreenCount != currentScreenCount) {
                AppData.isMultiScreen = isMultiScreen
                AppData.playerScreenDevice = screenDevice

                onScreenChange(isMultiScreen, screenDevice)

                lastScreenCount = currentScreenCount
            }

            delay(1000)
        }
    }
}
