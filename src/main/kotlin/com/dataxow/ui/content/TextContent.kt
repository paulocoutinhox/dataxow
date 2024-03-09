package com.dataxow.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope

@Composable
fun textContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    text: String,
    setText: (String) -> Unit,
    setPlayerWindowOpen: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            var currentText by remember { mutableStateOf(text) }

            TextField(
                value = currentText,
                onValueChange = {
                    currentText = it
                },
                label = { Text("Text to display") }
            )
            Button(onClick = {
                setText(currentText)
                setPlayerWindowOpen(true)
            }) {
                Text("Update Text")
            }
        }
    }
}
