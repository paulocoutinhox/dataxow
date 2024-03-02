package com.dataxow.model

import kotlinx.serialization.Serializable

@Serializable
data class FileListItem(
    val name: String,
    val isDirectory: Boolean,
)
