package com.dataxow.helper

import com.dataxow.app.AppData
import com.dataxow.model.FileListItem
import com.github.slugify.Slugify
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString

object VideoListHelper {
    fun loadVideosFromPath(path: String): List<FileListItem> {
        val videosPath = Paths.get(path).normalize()

        if (!videosPath.startsWith(Paths.get(AppData.config.project, "videos"))) {
            return listOf()
        }

        val directory: File = videosPath.toFile()
        if (!directory.exists() || !directory.isDirectory) {
            return listOf()
        }

        return prepareVideoList(directory)
            .map { obj: FileListItem ->
                FileListItem(
                    File(videosPath.absolutePathString(), obj.path).absolutePath,
                    obj.slug,
                    obj.isFile,
                    obj.isDirectory
                )
            }
    }

    fun prepareVideoList(directory: File): List<FileListItem> {
        val slugify = Slugify.builder().build()

        val fileListItems = Arrays.stream(directory.listFiles())
            .filter { file: File ->
                file.isDirectory || file.name.matches(".*\\.(avi|mov|mp4|mpeg)$".toRegex())
            }
            .map { obj: File -> FileListItem(obj.name, slugify.slugify(obj.name), obj.isFile, obj.isDirectory) }
            .collect(Collectors.toList())

        return fileListItems.sortedWith(Comparator.comparing(FileListItem::slug))
    }
}
