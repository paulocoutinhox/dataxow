package com.dataxow.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.dataxow.helper.FileHelper

@Composable
fun imageListContent(
    applicationScope: ApplicationScope,
    projectPath: String,
    imagePath: String?,
    setImagePath: (String?) -> Unit,
    setPlayerWindowOpen: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            val file = FileHelper.selectFile("Select Image", "image/*")
            setImagePath(file?.absolutePath)
            setPlayerWindowOpen(true)
        }) {
            Text("Select Image")
        }
    }
}
