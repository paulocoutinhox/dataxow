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
import androidx.compose.ui.window.Dialog
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
    searchResultsListState: LazyListState,
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
    var showAddUpdateDialog by remember { mutableStateOf(false) }

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
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = searchResultsListState
                        ) {
                            itemsIndexed(
                                items = searchResults,
                                key = { index, _ -> index },
                            ) { index, item ->
                                rowData(
                                    index = index,
                                    selected = searchResultsSelectedIndex == index,
                                    onTap = { selectedIndex ->
                                        searchResultsSelectedIndex = selectedIndex
                                    },
                                    onDoubleTap = { selectedIndex ->
                                        searchResultsSelectedIndex = selectedIndex
                                        val selectedItem = searchResults[searchResultsSelectedIndex]

                                        if (!AppData.textList.value.contains(selectedItem)) {
                                            val newList = AppData.textList.value + selectedItem
                                            AppData.textList.value = ArrayList(newList)
                                        }
                                    },
                                    content = {
                                        Column {
                                            Text(
                                                "• ${File(item.path).nameWithoutExtension}",
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
                                searchResultsSelectedIndex = -1
                                showAddUpdateDialog = true
                            }) {
                            Text("Add", style = LocalTextStyle.current.copy(textAlign = TextAlign.Center))
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            onClick = {
                                if (searchResultsSelectedIndex > -1) {
                                    showAddUpdateDialog = true
                                }
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

                                        val newList = ArrayList(searchResults).apply {
                                            removeAt(searchResultsSelectedIndex)
                                        }

                                        searchResults = newList

                                        if (searchResultsSelectedIndex >= searchResults.size) {
                                            searchResultsSelectedIndex = searchResults.size - 1
                                        } else {
                                            searchResultsSelectedIndex = -1
                                        }
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
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = contentSelectedListState
                        ) {
                            itemsIndexed(
                                items = textList,
                                key = { index, _ -> index },
                            ) { index, item ->
                                rowData(
                                    index = index,
                                    selected = contentSelectedSelectedIndex == index,
                                    onTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                    },
                                    onDoubleTap = { selectedIndex ->
                                        contentSelectedSelectedIndex = selectedIndex
                                        val selectedItem = textList[contentSelectedSelectedIndex]

                                        loadPreview(selectedItem)
                                    },
                                    content = {
                                        Column {
                                            Text(
                                                "• ${File(item.path).nameWithoutExtension}",
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
                                    val newList = ArrayList(AppData.textList.value).apply {
                                        removeAt(contentSelectedSelectedIndex)
                                    }

                                    AppData.textList.value = newList

                                    if (contentSelectedSelectedIndex >= newList.size) {
                                        contentSelectedSelectedIndex = newList.size - 1
                                    } else {
                                        contentSelectedSelectedIndex = -1
                                    }
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
                        itemsIndexed(
                            items = previewTextList,
                            key = { index, _ -> index },
                        ) { index, item ->
                            rowData(
                                index = index,
                                selected = index == AppData.liveTextIndex.value,
                                onTap = { selectedIndex ->
                                    val selectedItem = AppData.previewTextList.value[selectedIndex]

                                    AppData.liveText.value = selectedItem
                                    AppData.liveTextIndex.value = selectedIndex
                                    currentLiveText = AppData.liveText.value

                                    setPlayerWindowOpen(true)
                                },
                                onDoubleTap = { selectedIndex ->
                                    val selectedItem = AppData.previewTextList.value[selectedIndex]

                                    AppData.liveText.value = selectedItem
                                    AppData.liveTextIndex.value = selectedIndex
                                    currentLiveText = AppData.liveText.value

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

    if (showAddUpdateDialog) {
        var selectedItem: FileListItem? = null

        if (searchResultsSelectedIndex > -1) {
            selectedItem = searchResults[searchResultsSelectedIndex]
        }

        fileEditDialog(
            selectedItem = selectedItem,
            onClose = {
                showAddUpdateDialog = false
            },
        )
    }
}

fun search(query: String): List<FileListItem> {
    if (query.isEmpty()) {
        return arrayListOf()
    }

    val slugify = Slugify.builder().build()
    val slugQuery = slugify.slugify(query)

    return TextListHelper.loadTextsFromPath(File(AppData.config.project, "texts").absolutePath)
        .filter { obj: FileListItem ->
            obj.slug.contains(slugQuery)
        }
}

fun loadPreview(item: FileListItem) {
    AppData.liveTextIndex.value = -1
    AppData.previewTextList.value = ArrayList(TextHelper.loadTextFromPath(item.path))
}

@Composable
fun fileEditDialog(
    selectedItem: FileListItem?,
    onClose: () -> Unit
) {
    val isEditing = selectedItem != null
    var fileName by remember { mutableStateOf(selectedItem?.path ?: "") }
    var fileContent by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedItem) {
        if (isEditing) {
            fileContent = File(selectedItem?.path ?: "").readText()
        }
    }

    val dialogTitle = if (isEditing) "Edit File" else "Add New File"

    Dialog(onDismissRequest = onClose) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            modifier = Modifier.padding(40.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize()
            ) {
                Text(dialogTitle, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))

                if (!isEditing) {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = {
                            fileName = it
                            errorMessage = null
                        },
                        label = { Text("File Name") },
                        readOnly = isEditing,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = fileContent,
                    onValueChange = { fileContent = it },
                    label = { Text("File Content") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colors.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (fileName.isBlank()) {
                            errorMessage = "File name cannot be empty."
                            return@Button
                        }

                        try {
                            if (isEditing) {
                                File(selectedItem?.path ?: "").writeText(fileContent.trim())
                            } else {
                                val file = File(File(AppData.config.project, "texts"), "$fileName.txt")
                                file.writeText(fileContent.trim())
                            }
                            onClose()
                        } catch (e: Exception) {
                            errorMessage = "Error saving file: ${e.message}"
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
