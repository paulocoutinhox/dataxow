package com.dataxow.ui.content

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
    isGrid: Boolean,
    setIsGrid: (Boolean) -> Unit,
    listState: LazyListState,
    gridState: LazyGridState,
    reload: () -> Unit,
) {
    val imageList = AppData.imageList.collectAsState()

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
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                setIsGrid(!isGrid)
            }) {
                if (isGrid) {
                    Text("Show List")
                } else {
                    Text("Show Grid")
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        if (isGrid) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.padding(8.dp),
                state = gridState,
            ) {
                items(count = imageList.value.size, itemContent = { index ->
                    val image = imageList.value[index]

                    if (image.isFile) {
                        AsyncImage(
                            model = File(image.path),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            setImagePath(image.path)
                                            setPlayerWindowOpen(true)
                                        }
                                    )
                                },
                        )
                    }
                })
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(8.dp),
                state = listState
            ) {
                items(imageList.value) { image ->
                    if (image.isFile) {
                        Row {
                            AsyncImage(
                                model = File(image.path),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                setImagePath(image.path)
                                                setPlayerWindowOpen(true)
                                            }
                                        )
                                    },
                            )
                            Text(
                                text = File(image.path).name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.CenterVertically)
                                    .padding(8.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                setImagePath(image.path)
                                                setPlayerWindowOpen(true)
                                            }
                                        )
                                    }
                            )
                        }

                        Divider()
                    }
                }
            }
        }
    }
}
