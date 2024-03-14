package com.dataxow.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.app.AppData
import com.dataxow.helper.TextListHelper
import com.dataxow.model.FileListItem
import com.dataxow.ui.components.rowData
import java.io.File

@Composable
fun textContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    setPlayerWindowOpen: (Boolean) -> Unit,
    previewListState: LazyListState,
    contentSelectedListState: LazyListState,
    contentSelectedPreviewListState: LazyListState,
) {
    val liveText = AppData.liveText.collectAsState().value
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<FileListItem>()) }
    var searchResultsSelectedIndex by remember { mutableStateOf(-1) }
    val textList = AppData.textList.collectAsState().value
    val previewTextList = AppData.previewTextList.collectAsState().value
    var contentSelectedSelectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(searchText) {
        searchResults = search(searchText)
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    BasicText("Search a File:", style = TextStyle(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    Button(
                        onClick = { searchResults = search(searchText) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Update", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    BasicText("Search Results:", style = TextStyle(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(6.dp))

                    if (searchResults.isEmpty()) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    "Empty",
                                    modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                )
                            } else {
                                Text(
                                    "Not Found",
                                    modifier = Modifier.weight(1f).fillMaxSize().wrapContentSize(Alignment.Center),
                                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                                )
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f), state = previewListState) {
                            itemsIndexed(items = searchResults, key = { _, item -> item.path }) { index, item ->
                                rowData(
                                    index = index,
                                    selected = searchResultsSelectedIndex == index,
                                    onTap = { selectedIndex ->
                                        searchResultsSelectedIndex = selectedIndex
                                    },
                                    onDoubleTap = { selectedIndex ->
                                        searchResultsSelectedIndex = selectedIndex

                                        if (!AppData.textList.value.contains(item)) {
                                            val newList = AppData.textList.value + item
                                            AppData.textList.value = ArrayList(newList)
                                        }
                                    },
                                    content = {
                                        Column {
                                            Text(
                                                "• ${File(item.path).name}",
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            )
                                            Divider()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Divider(Modifier.padding(bottom = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                // ignore
                            }) {
                            Text("Add", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                // ignore
                            }) {
                            Text("Change", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                // ignore
                            }) {
                            Text("Delete", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                    }
                    Divider(Modifier.padding(vertical = 6.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    BasicText("Content Selected:", style = TextStyle(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(6.dp))

                    if (textList.isEmpty()) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Empty",
                                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                                style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f), state = contentSelectedListState) {
                            itemsIndexed(items = textList, key = { index, item -> item.path }) { index, item ->
                                rowData(
                                    index = index,
                                    selected = contentSelectedSelectedIndex == index,
                                    onTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                    },
                                    onDoubleTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                    },
                                    content = {
                                        Column {
                                            Text(
                                                "• ${File(item.path).name}",
                                                modifier = Modifier.padding(vertical = 5.dp)
                                            )
                                            Divider()
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Divider(Modifier.padding(vertical = 6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                // ignore
                            }) {
                            Text("Remove", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                //
                            }) {
                            Text("Send to Preview", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                    }
                }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                BasicText("Selected Content Preview:", style = TextStyle(fontWeight = FontWeight.Bold))

                if (previewTextList.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Empty",
                            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                            style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f), state = contentSelectedPreviewListState) {
                        items(items = previewTextList, key = { item -> item }) { item ->
                            Text("• $item", modifier = Modifier.padding(2.dp))
                            Divider()
                        }
                    }
                }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                var currentText by remember { mutableStateOf(liveText) }

                BasicText("Live Text:", style = TextStyle(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    value = currentText,
                    onValueChange = {
                        currentText = it
                    },
                    label = { Text("Text to Display") }
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                AppData.liveText.value = currentText
                                setPlayerWindowOpen(true)
                            }) {
                            Text("Update Live Text", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(6.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentText = ""
                            }) {
                            Text("Clear", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                    }
                }
            }
        }
    }
}

fun search(query: String): List<FileListItem> {
    if (query.isEmpty()) {
        return listOf()
    }

    return TextListHelper.loadTextsFromPath(File(AppData.config.project, "songs").absolutePath)
        .filter { obj: FileListItem ->
            File(obj.path).name.contains(query)
        }
}
