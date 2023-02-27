package com.andrew.tasky.agenda.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapConverters {
    fun bitmapToCompressByteArray(bitmap: Bitmap, targetSize: Int): ByteArray? {
        val outputStream = ByteArrayOutputStream()
        var quality = 100
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        var result = outputStream.toByteArray()
        while (result.size > targetSize && quality > 0) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            result = outputStream.toByteArray()
            quality -= 5
            if (quality == 0) {
                return null
            }
        }
        return result
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}
