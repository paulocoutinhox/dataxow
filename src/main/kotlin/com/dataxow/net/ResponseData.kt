package com.dataxow.net

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
