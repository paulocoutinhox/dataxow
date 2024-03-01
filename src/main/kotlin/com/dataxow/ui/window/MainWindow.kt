package com.dataxow.ui.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import com.dataxow.helper.FileHelper
import com.dataxow.helper.ImageHelper
import com.dataxow.ui.components.ipSelector
import javax.swing.JFileChooser

@Composable
fun mainWindow(
    applicationScope: ApplicationScope,
    projectPath: String,
    setProjectPath: (String) -> Unit,
    text: String,
    setText: (String) -> Unit,
    imagePath: String?,
    setImagePath: (String?) -> Unit,
    videoPath: String?,
    setVideoPath: (String?) -> Unit,
    playerWindowOpen: Boolean,
    setPlayerWindowOpen: (Boolean) -> Unit,
    ips: List<String>,
    serverHost: String,
    setServerHost: (String) -> Unit,
    serverPort: String,
    setServerPort: (String) -> Unit,
    qrCodeBitmap: ImageBitmap?,
    setQrCodeBitmap: (ImageBitmap?) -> Unit,
    serverStatus: Boolean,
    setServerStatus: (Boolean) -> Unit,
    startServer: (String, Int) -> Unit,
    stopServer: () -> Unit,
) {
    Window(onCloseRequest = applicationScope::exitApplication, title = "DataXow") {
        Scaffold {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    TextField(
                        value = projectPath,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Project Folder") }
                    )
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
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text,
                        onValueChange = { setText(it) },
                        label = { Text("Text to display") }
                    )
                    Button(onClick = { setPlayerWindowOpen(true) }) {
                        Text("Update Text")
                    }
                }
                Button(onClick = {
                    val file = FileHelper.selectFile("Select Image", "image/*")
                    setImagePath(file?.absolutePath)
                    setVideoPath(null)
                    setPlayerWindowOpen(true)
                }) {
                    Text("Select Image")
                }
                Button(onClick = {
                    val file = FileHelper.selectFile("Select Video", "video/*")
                    setVideoPath(file?.absolutePath)
                    setImagePath(null)
                    setPlayerWindowOpen(true)
                }) {
                    Text("Select Video")
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ipSelector(items = ips, selectedItem = serverHost, onItemSelected = { setServerHost(it) })
                        TextField(value = serverPort, onValueChange = { setServerPort(it) }, label = { Text("Port") })
                    }
                    Row {
                        Button(onClick = {
                            if (!serverStatus) {
                                startServer(serverHost, serverPort.toInt())
                                setQrCodeBitmap(
                                    ImageHelper.generateQRCode(
                                        "http://$serverHost:$serverPort/rcontrol/?api_url=http://$serverHost:$serverPort",
                                    )
                                )
                                setServerStatus(true)
                            } else {
                                stopServer()
                                setQrCodeBitmap(null)
                                setServerStatus(false)
                            }
                        }) {
                            Text(if (!serverStatus) "Start Server" else "Stop Server")
                        }
                    }
                    qrCodeBitmap?.let { image ->
                        Image(bitmap = image, contentDescription = "QR Code")
                    }
                }
            }
        }
    }
}
