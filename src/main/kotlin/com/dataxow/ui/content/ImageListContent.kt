package com.dataxow.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.app.AppData
import com.dataxow.helper.FileHelper
import com.dataxow.ui.components.rowData
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
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
    showListImage: Boolean,
    setShowListImage: (Boolean) -> Unit,
    reload: () -> Unit,
) {
    val imageList = AppData.imageList.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                val file = FileHelper.selectFile("Open Image")
                setImagePath(file?.absolutePath)
                setPlayerWindowOpen(true)
            }) {
                Text("Open Image", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                reload()
            }) {
                Text("Refresh", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                setIsGrid(!isGrid)
            }) {
                if (isGrid) {
                    Text("Show List", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                } else {
                    Text("Show Grid", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Checkbox(
                checked = showListImage,
                onCheckedChange = setShowListImage
            )
            Text("Show List Image")
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
                        rowData(
                            index = index,
                            selected = false,
                            onDoubleTap = {
                                setImagePath(image.path)
                                setPlayerWindowOpen(true)
                            },
                            content = {
                                Column {
                                    KamelImage(
                                        resource = asyncPainterResource(data = File(image.path)),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp),
                                    )
                                }
                            }
                        )
                    }
                })
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(8.dp),
                state = listState
            ) {
                itemsIndexed(imageList.value) { index, image ->
                    if (image.isFile) {
                        rowData(
                            index = index,
                            selected = false,
                            onDoubleTap = {
                                setImagePath(image.path)
                                setPlayerWindowOpen(true)
                            },
                            content = {
                                Column {
                                    Row {
                                        if (showListImage) {
                                            KamelImage(
                                                resource = asyncPainterResource(data = File(image.path)),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .padding(8.dp),
                                            )
                                            Text(
                                                text = File(image.path).nameWithoutExtension,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.CenterVertically)
                                                    .padding(8.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "• ${File(image.path).nameWithoutExtension}",
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .align(Alignment.CenterVertically)
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                    Divider()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
