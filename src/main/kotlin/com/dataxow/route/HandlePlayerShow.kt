package com.dataxow.route

import com.dataxow.app.AppData
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.handlePlayerShow(requestData: RequestData) {
    AppData.onShowPlayer?.invoke()

    respond(
        ResponseData(
            true, message = "updated", data = mapOf("" to "")
        )
    )
}
