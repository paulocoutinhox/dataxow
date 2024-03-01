package com.dataxow.helper

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Rect

class ImageHelper {
    companion object {
        fun generateQRCode(data: String): ImageBitmap {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap()
            bitmap.allocN32Pixels(width, height)

            val canvas = org.jetbrains.skia.Canvas(bitmap)
            val paint = org.jetbrains.skia.Paint().apply {
                color = org.jetbrains.skia.Color.BLACK
                isAntiAlias = false
            }

            canvas.clear(org.jetbrains.skia.Color.WHITE)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        canvas.drawRect(Rect.makeXYWH(x.toFloat(), y.toFloat(), 1f, 1f), paint)
                    }
                }
            }

            return bitmap.asComposeImageBitmap()
        }
    }
}
