package com.dataxow.route

import com.dataxow.app.AppData
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

suspend fun ApplicationCall.handlePlayerUpdate(requestData: RequestData) {
    val type = requestData.params?.get("type") ?: return

    if (type == "text") {
        val newText = requestData.params["text"]

        if (newText != null) {
            AppData.onTextUpdate?.invoke(newText)
        }

        respond(
            ResponseData(
                true, message = "updated", data = mapOf("" to "")
            )
        )
    } else if (type == "image") {
        var requestedPath = requestData.params["path"] ?: return
        requestedPath = requestedPath.replace("..", "")

        val imagesPath = Paths.get(AppData.config.project, "images", requestedPath).normalize()

        if (!imagesPath.startsWith(Paths.get(AppData.config.project, "images"))) {
            respond(HttpStatusCode.NotFound, "Invalid Path")
            return
        }

        AppData.onImageUpdate?.invoke(imagesPath.absolutePathString())

        respond(
            ResponseData(
                true, message = "updated", data = mapOf("" to "")
            )
        )
    } else {
        handleNotFound()
    }
}
