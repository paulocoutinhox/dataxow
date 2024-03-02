package com.dataxow.route

import com.dataxow.app.AppData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.pathString

suspend fun ApplicationCall.handleGetImage() {
    val requestedPath = parameters["path"] ?: ""
    val safePath = requestedPath.replace("..", "")

    val imagePath = File(File(AppData.config.project, "images"), safePath).normalize()

    if (!imagePath.path.startsWith(Paths.get(AppData.config.project, "images").pathString) || !imagePath.isFile) {
        respond(HttpStatusCode.NotFound, "Image not found or path is invalid")
        return
    }

    respondFile(imagePath)
}
