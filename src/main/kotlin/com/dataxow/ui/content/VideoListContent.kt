package com.dataxow.ui.content

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.app.AppData
import com.dataxow.helper.FileHelper
import java.io.File

@Composable
fun videoListContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    videoPath: String?,
    setVideoPath: (String?) -> Unit,
    setPlayerWindowOpen: (Boolean) -> Unit,
    listState: LazyListState,
    reload: () -> Unit,
) {
    val videoList by remember { mutableStateOf(AppData.videoList) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Button(onClick = {
                val file = FileHelper.selectFile("Open Video", "video/*")
                setVideoPath(file?.absolutePath)
                setPlayerWindowOpen(true)
            }) {
                Text("Open Video")
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                reload()
            }) {
                Text("Refresh")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        LazyColumn(
            modifier = Modifier.padding(8.dp),
            state = listState,
        ) {
            items(videoList.value) { video ->
                if (video.isFile) {
                    Text(
                        text = "• ${File(video.path).name}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        setVideoPath(video.path)
                                        setPlayerWindowOpen(true)
                                    }
                                )
                            }
                    )
                    Divider()
                }
            }
        }
    }
}
