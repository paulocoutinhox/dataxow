package com.dataxow.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.helper.ImageHelper
import com.dataxow.helper.SystemHelper
import com.dataxow.ui.components.ipSelector

@Composable
fun remoteContent(
    applicationScope: ApplicationScope,
    projectPath: String,
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
    Column(modifier = Modifier.padding(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            BasicText("Server Host:", style = TextStyle(fontWeight = FontWeight.Bold))
            ipSelector(
                modifier = Modifier.fillMaxWidth(),
                items = ips,
                selectedItem = serverHost,
                onItemSelected = { setServerHost(it) })
            BasicText("Server Port:", style = TextStyle(fontWeight = FontWeight.Bold))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = serverPort,
                onValueChange = { setServerPort(it) },
                label = { Text("Port") },
                singleLine = true,
            )
        }
        Row(modifier = Modifier.padding(top = 10.dp)) {
            Button(onClick = {
                if (!serverStatus) {
                    startServer(serverHost, serverPort.toInt())
                    setQrCodeBitmap(
                        ImageHelper.generateQRCode(
                            "http://$serverHost:$serverPort/rcontrol/?api=http://$serverHost:$serverPort",
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

            if (serverStatus) {
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    SystemHelper.openWebPage("http://$serverHost:$serverPort/rcontrol/?api=http://$serverHost:$serverPort")
                }) {
                    Text("Open Browser")
                }
            }
        }
        qrCodeBitmap?.let { image ->
            Divider(modifier = Modifier.padding(vertical = 10.dp))
            BasicText(
                "Scan To Remote Control",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Image(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                bitmap = image,
                contentDescription = "QR Code"
            )
        }
    }
}
