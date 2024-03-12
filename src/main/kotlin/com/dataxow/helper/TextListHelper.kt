package com.dataxow.helper

import com.dataxow.app.AppData
import com.dataxow.model.FileListItem
import java.io.File
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.io.path.absolutePathString

object TextListHelper {
    fun loadTextsFromPath(path: String): List<FileListItem> {
        val textsPath = Paths.get(path).normalize()

        if (!textsPath.startsWith(Paths.get(AppData.config.project, "songs"))) {
            return listOf()
        }

        val directory: File = textsPath.toFile()
        if (!directory.exists() || !directory.isDirectory) {
            return listOf()
        }

        return prepareTextList(directory)
            .map { obj: FileListItem ->
                FileListItem(
                    File(textsPath.absolutePathString(), obj.path).absolutePath,
                    obj.isFile,
                    obj.isDirectory
                )
            }
    }

    fun prepareTextList(directory: File): List<FileListItem> {
        return Arrays.stream(directory.listFiles())
            .filter { file: File ->
                file.isDirectory || file.name.matches(".*\\.(txt)$".toRegex())
            }
            .sorted(Comparator.comparing(File::getName))
            .map { obj: File -> FileListItem(obj.name, obj.isFile, obj.isDirectory) }
            .collect(Collectors.toList())
    }
}
