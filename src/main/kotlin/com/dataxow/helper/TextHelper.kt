package com.dataxow.helper

import com.dataxow.app.AppData
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

object TextHelper {
    fun loadTextFromPath(path: String): List<String> {
        val textPath = Paths.get(path).normalize()

        if (!textPath.startsWith(Paths.get(AppData.config.project, "texts"))) {
            return listOf()
        }

        if (!textPath.exists() || !textPath.isRegularFile()) {
            return listOf()
        }

        try {
            val content = Files.readString(textPath)

            // Break by one or more empty lines and trim each section, removing empty sections
            return content.split("\\R{2,}".toRegex())
                .map { it.trim() } // Trim each section
                .filter { it.isNotEmpty() } // Remove empty sections
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return listOf()
    }
}
