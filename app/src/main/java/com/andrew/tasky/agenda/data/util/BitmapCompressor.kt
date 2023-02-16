package com.andrew.tasky.agenda.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

class BitmapCompressor {

    fun compressByteArray(byteArray: ByteArray, targetSize: Int): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        var quality = 100
        var result = byteArray
        while (result.size > targetSize && quality > 0) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            result = stream.toByteArray()
            quality -= 5
        }
        return result
    }
}
