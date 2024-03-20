package com.dataxow.helper

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

object FileHelper {
    fun selectFile(title: String): File? {
        val fileDialog = FileDialog(null as Frame?, title, FileDialog.LOAD)
        fileDialog.isVisible = true
        return fileDialog.files.firstOrNull()
    }
}
