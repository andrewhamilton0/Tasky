package com.andrew.tasky.agenda.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BitmapConverters {
    suspend fun bitmapToCompressByteArray(bitmap: Bitmap, targetSize: Int): ByteArray? {
        return withContext(Dispatchers.Default) {
            val outputStream = ByteArrayOutputStream()
            var quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var result = outputStream.toByteArray()
            while (result.size > targetSize && quality > 0) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                result = outputStream.toByteArray()
                quality -= 5
                if (quality == 0) {
                    return@withContext null
                }
            }
            result
        }
    }

    suspend fun byteArrayToBitmap(data: ByteArray): Bitmap? {
        return try {
            withContext(Dispatchers.IO) {
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }
        } catch (e: Exception) {
            Log.e("Bitmap Converters fun byteArrayToBitmap", e.message ?: "Unknown error")
            null
        }
    }
}
