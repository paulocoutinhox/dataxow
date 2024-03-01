package com.dataxow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ipSelector(items: List<String>, selectedItem: String, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val currentSelectedItem = items.firstOrNull { it == selectedItem } ?: items.firstOrNull()

    Box {
        TextField(
            value = currentSelectedItem ?: "",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { label ->
                DropdownMenuItem(onClick = {
                    onItemSelected(label)
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
