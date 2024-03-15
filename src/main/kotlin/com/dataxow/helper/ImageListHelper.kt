package com.dataxow.helper

import com.dataxow.app.AppData
import com.dataxow.model.FileListItem
import com.github.slugify.Slugify
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString

object ImageListHelper {
    fun loadImagesFromPath(path: String): List<FileListItem> {
        val imagesPath = Paths.get(path).normalize()

        if (!imagesPath.startsWith(Paths.get(AppData.config.project, "images"))) {
            return listOf()
        }

        val directory: File = imagesPath.toFile()
        if (!directory.exists() || !directory.isDirectory) {
            return listOf()
        }

        return prepareImageList(directory)
            .map { obj: FileListItem ->
                FileListItem(
                    File(imagesPath.absolutePathString(), obj.path).absolutePath,
                    obj.slug,
                    obj.isFile,
                    obj.isDirectory
                )
            }
    }

    fun prepareImageList(directory: File): List<FileListItem> {
        val slugify = Slugify.builder().build()

        val fileListItems = Arrays.stream(directory.listFiles())
            .filter { file: File ->
                file.isDirectory || file.name.matches(".*\\.(jpg|jpeg|png|gif|webp)$".toRegex())
            }
            .map { obj: File -> FileListItem(obj.name, slugify.slugify(obj.name), obj.isFile, obj.isDirectory) }
            .collect(Collectors.toList())

        return fileListItems.sortedWith(Comparator.comparing(FileListItem::slug))
    }
}
