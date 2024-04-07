package com.dataxow.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.app.AppData
import javax.swing.JFileChooser

@Composable
fun homeContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    setProjectPath: (String) -> Unit,
    setForceUpdateWindowState: () -> Unit,
    reload: () -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("Project Folder:", style = TextStyle(fontWeight = FontWeight.Bold))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = projectPath,
                onValueChange = {},
                label = { Text("Project Folder") },
                readOnly = true,
                singleLine = true,
            )
            Row {
                Button(onClick = {
                    val chooser = JFileChooser().apply {
                        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    }

                    val result = chooser.showOpenDialog(null)

                    if (result == JFileChooser.APPROVE_OPTION) {
                        setProjectPath(chooser.selectedFile.absolutePath)
                    }
                }) {
                    Text("Select Folder")
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    reload()
                }) {
                    Text("Reload")
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("System Screen:", style = TextStyle(fontWeight = FontWeight.Bold))
            Button(onClick = { setForceUpdateWindowState() }) {
                Text("Refresh Screens")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("Player:", style = TextStyle(fontWeight = FontWeight.Bold))
            Button(onClick = {
                AppData.onClosePlayer?.invoke()
            }) {
                Text("Close")
            }
        }
    }
}
