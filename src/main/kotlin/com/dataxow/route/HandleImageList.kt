package com.dataxow.route

import com.dataxow.app.AppData
import com.dataxow.helper.ImageListHelper
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.File
import java.nio.file.Paths


suspend fun ApplicationCall.handleImageList(requestData: RequestData) {
    var requestedPath = requestData.params?.getOrDefault("path", "") ?: ""
    requestedPath = requestedPath.replace("..", "")

    val imagesPath = Paths.get(AppData.config.project, "images", requestedPath).normalize()

    if (!imagesPath.startsWith(Paths.get(AppData.config.project, "images"))) {
        respond(HttpStatusCode.NotFound, "Invalid Path")
        return
    }

    val directory: File = imagesPath.toFile()
    if (!directory.exists() || !directory.isDirectory) {
        respond(HttpStatusCode.NotFound, "Path Not Found")
        return
    }

    val list = ImageListHelper.prepareImageList(directory)

    respond(ResponseData(true, "list", mapOf("list" to list)))
}
