package com.dataxow.ui.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.app.AppData
import com.dataxow.helper.TextListHelper
import com.dataxow.model.FileListItem
import java.io.File

@Composable
fun textContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    text: String,
    setText: (String) -> Unit,
    setPlayerWindowOpen: (Boolean) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var customText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<FileListItem>()) }
    var selectedSongs by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(searchText) {
        searchResults = searchSongs(searchText)
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
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
                    onClick = { searchResults = searchSongs(searchText) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Update")
                }
                Divider(Modifier.padding(vertical = 8.dp))
                BasicText("Search Results:", style = TextStyle(fontWeight = FontWeight.Bold))
                Spacer(Modifier.height(6.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = searchResults, key = { item -> item.path }) { song ->
                        Text("• ${File(song.path).name}", modifier = Modifier.padding(vertical = 5.dp))
                        Divider()
                    }
                }
                Divider(Modifier.padding(bottom = 6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        onClick = { /* Action to add song */ }) {
                        Text("Add")
                    }
                    Spacer(Modifier.width(4.dp))
                    Button(
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        onClick = { /* Action to change song */ }) {
                        Text("Change")
                    }
                    Spacer(Modifier.width(4.dp))
                    Button(
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        onClick = { /* Action to delete song */ }) {
                        Text("Delete")
                    }
                }
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                BasicText("Selected Content:", style = TextStyle(fontWeight = FontWeight.Bold))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(items = selectedSongs, key = { item -> item }) { song ->
                        Text(song, modifier = Modifier.padding(2.dp))
                        Divider()
                    }
                }
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Custom Text") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Divider(Modifier.fillMaxHeight().width(1.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Button(onClick = { /* Action to clear selection */ }) {
                    Text("Clear")
                }
                Spacer(Modifier.height(6.dp))
                Button(onClick = { /* Action to go live */ }) {
                    Text("Go Live")
                }
            }
        }
    }
}

fun searchSongs(query: String): List<FileListItem> {
    if (query.isEmpty()) {
        return listOf()
    }

    return TextListHelper.loadTextsFromPath(File(AppData.config.project, "songs").absolutePath)
        .filter { obj: FileListItem ->
            File(obj.path).name.contains(query)
        }
}


//    Column(modifier = Modifier.padding(16.dp)) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            var currentText by remember { mutableStateOf(text) }
//
//            TextField(
//                value = currentText,
//                onValueChange = {
//                    currentText = it
//                },
//                label = { Text("Text to display") }
//            )
//            Button(onClick = {
//                setText(currentText)
//                setPlayerWindowOpen(true)
//            }) {
//                Text("Update Text")
//            }
//        }
//    }
