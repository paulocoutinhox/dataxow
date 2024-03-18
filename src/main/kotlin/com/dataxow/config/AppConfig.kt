package com.dataxow.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    var project: String = "",
    var serverPort: String = "10123",
    var showListImage: Boolean = true,
)
