package com.dataxow.model

import kotlinx.serialization.Serializable

@Serializable
data class FileListItem(
    val path: String,
    val isDirectory: Boolean,
)
