package com.dataxow.ui.content

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import coil3.compose.AsyncImage
import com.dataxow.app.AppData
import com.dataxow.helper.FileHelper
import java.io.File

@Composable
fun imageListContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    imagePath: String?,
    setImagePath: (String?) -> Unit,
    setPlayerWindowOpen: (Boolean) -> Unit,
    reload: () -> Unit,
) {
    val imageList by remember { mutableStateOf(AppData.imageList) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            Button(onClick = {
                val file = FileHelper.selectFile("Open Image", "image/*")
                setImagePath(file?.absolutePath)
                setPlayerWindowOpen(true)
            }) {
                Text("Open Image")
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                reload()
            }) {
                Text("Refresh")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 10.dp))
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            items(count = imageList.value.size, itemContent = { index ->
                AsyncImage(
                    model = File(imageList.value[index].path),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    setImagePath(imageList.value[index].path)
                                    setPlayerWindowOpen(true)
                                }
                            )
                        },
                )
            })
        }
    }
}
