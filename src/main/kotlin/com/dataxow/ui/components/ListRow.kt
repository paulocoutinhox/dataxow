package com.dataxow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun rowData(
    index: Int,
    selected: Boolean,
    onTap: ((Int) -> Unit)? = null,
    onDoubleTap: ((Int) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .background(rowColor(selected))
            .clickable {
                // ignore
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTap?.invoke(index)
                    },
                    onDoubleTap = {
                        onDoubleTap?.invoke(index)
                    }
                )
            },
    ) {
        content()
    }
}

@Composable
private fun rowColor(selected: Boolean): Color {
    return if (selected) {
        Color(0xffb3e5fc)
    } else {
        Color.Transparent
    }
}
