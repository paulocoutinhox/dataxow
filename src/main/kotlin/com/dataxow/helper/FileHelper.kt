package com.dataxow.helper

import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class FileHelper {
    companion object {
        fun selectFile(title: String, fileType: String): File? {
            val fileDialog = FileDialog(Frame(), title, FileDialog.LOAD)
            fileDialog.isVisible = true
            return fileDialog.files.firstOrNull()
        }
    }
}
