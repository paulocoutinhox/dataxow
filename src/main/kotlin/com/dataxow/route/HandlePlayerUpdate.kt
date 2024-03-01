package com.dataxow.route

import com.dataxow.app.AppData
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.handlePlayerUpdate(requestData: RequestData) {
    val type = requestData.params?.get("type") ?: return

    if (type == "text") {
        val newText = requestData.params["text"]

        if (newText != null) {
            AppData.onTextUpdate?.invoke(newText)
        }

        respond(
            ResponseData(
                true, data = mapOf(
                    "func" to requestData.func.toString(),
                    "params" to requestData.params.toString(),
                )
            )
        )
    } else {
        handleNotFound()
    }
}
