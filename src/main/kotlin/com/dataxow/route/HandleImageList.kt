package com.dataxow.route

import com.dataxow.app.AppData
import com.dataxow.model.FileListItem
import com.dataxow.net.RequestData
import com.dataxow.net.ResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors


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

    val list = Arrays.stream(directory.listFiles())
        .filter { file: File ->
            file.isDirectory || file.name.matches(".*\\.(jpg|jpeg|png|gif|webp)$".toRegex())
        }
        .sorted(Comparator.comparing(File::getName))
        .map { obj: File -> FileListItem(obj.name, obj.isDirectory) }
        .collect(Collectors.toList())

    respond(ResponseData<Map<String, List<FileListItem>>>(true, "list", mapOf("list" to list)))
}
