package com.dataxow.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.dataxow.helper.TextHelper
import com.dataxow.helper.TextListHelper
import com.dataxow.model.FileListItem
import com.dataxow.ui.components.rowData
import com.github.slugify.Slugify
import java.io.File
import javax.swing.JOptionPane


@Composable
fun textContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    setPlayerWindowOpen: (Boolean) -> Unit,
    previewListState: LazyListState,
    contentSelectedListState: LazyListState,
    contentSelectedPreviewListState: LazyListState,
) {
    var currentLiveText by remember { mutableStateOf(AppData.liveText.value) }
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<FileListItem>()) }
    var searchResultsSelectedIndex by remember { mutableStateOf(-1) }
    val textList = AppData.textList.collectAsState().value
    val previewTextList = AppData.previewTextList.collectAsState().value
    var contentSelectedSelectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(searchText) {
        searchResultsSelectedIndex = -1
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
                        onClick = {
                            searchResultsSelectedIndex = -1
                            searchResults = search(searchText)
                        },
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
                                if (searchResultsSelectedIndex > -1 && searchResultsSelectedIndex < searchResults.size) {
                                    val file = searchResults[searchResultsSelectedIndex]

                                    val option = JOptionPane.showConfirmDialog(
                                        null,
                                        "Are you sure?",
                                        "Message",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE
                                    )

                                    if (option == JOptionPane.OK_OPTION) {
                                        File(file.path).delete()

                                        val newList = ArrayList(searchResults)
                                        newList.removeAt(searchResultsSelectedIndex)
                                        searchResults = newList
                                    }
                                }
                            }) {
                            Text("Delete", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                    }
                    Divider(Modifier.padding(vertical = 6.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    BasicText("Selected Content:", style = TextStyle(fontWeight = FontWeight.Bold))
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
                            itemsIndexed(items = textList, key = { _, item -> item.path }) { index, item ->
                                rowData(
                                    index = index,
                                    selected = contentSelectedSelectedIndex == index,
                                    onTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                    },
                                    onDoubleTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                        loadPreview(item)
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
                                if (contentSelectedSelectedIndex > -1 && contentSelectedSelectedIndex < AppData.textList.value.size) {
                                    val newList = ArrayList(AppData.textList.value)
                                    newList.removeAt(contentSelectedSelectedIndex)
                                    AppData.textList.value = newList
                                    contentSelectedSelectedIndex = -1
                                }
                            }) {
                            Text("Remove", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                if (contentSelectedSelectedIndex > -1 && contentSelectedSelectedIndex < AppData.textList.value.size) {
                                    val item = AppData.textList.value[contentSelectedSelectedIndex]
                                    loadPreview(item)
                                }
                            }) {
                            Text("Send to Preview", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                    }
                }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                BasicText("Preview Selected Content:", style = TextStyle(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))

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
                        itemsIndexed(items = previewTextList, key = { _, item -> item }) { index, item ->
                            rowData(
                                index = index,
                                selected = index == AppData.liveTextIndex.value,
                                onTap = {
                                    AppData.liveText.value = item
                                    AppData.liveTextIndex.value = index
                                    currentLiveText = AppData.liveText.value
                                    setPlayerWindowOpen(true)
                                },
                                onDoubleTap = {
                                    AppData.liveText.value = item
                                    AppData.liveTextIndex.value = index
                                    setPlayerWindowOpen(true)
                                },
                                content = {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("•", modifier = Modifier.padding(2.dp))
                                            Text(item, modifier = Modifier.padding(2.dp))
                                        }
                                        Divider()
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                BasicText("Live Text:", style = TextStyle(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    value = currentLiveText,
                    onValueChange = {
                        currentLiveText = it
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
                                AppData.liveText.value = currentLiveText
                                setPlayerWindowOpen(true)
                            }) {
                            Text("Update Live Text", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(6.dp))
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                currentLiveText = ""
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
        return arrayListOf()
    }

    val slugify = Slugify.builder().build()
    val slugQuery = slugify.slugify(query)

    return TextListHelper.loadTextsFromPath(File(AppData.config.project, "songs").absolutePath)
        .filter { obj: FileListItem ->
            obj.slug.contains(slugQuery)
        }
}

fun loadPreview(item: FileListItem) {
    AppData.liveTextIndex.value = -1
    AppData.previewTextList.value = ArrayList(TextHelper.loadTextFromPath(item.path))
}
