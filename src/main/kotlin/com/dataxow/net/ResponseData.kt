package com.dataxow.net

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val success: Boolean,
    val message: String? = null,
    val data: Map<String, String>? = null
)
