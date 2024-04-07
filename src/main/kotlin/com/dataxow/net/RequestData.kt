package com.dataxow.net

import kotlinx.serialization.Serializable

@Serializable
data class RequestData(
    val func: String? = null,
    val params: Map<String, String>? = null
)
