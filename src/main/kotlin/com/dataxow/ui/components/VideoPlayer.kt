package com.dataxow.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dataxow.renderer.RenderCallbackAdapter

@Composable
fun videoPlayer(
    adapter: RenderCallbackAdapter,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        videoPlayerFrame(adapter.imageBitmap)
    }
}
