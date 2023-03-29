package com.andrew.tasky.agenda.data.storage

import android.content.Context
import android.graphics.Bitmap
import com.andrew.tasky.agenda.data.util.BitmapConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

class ImageStorage(private val context: Context) {
    suspend fun saveImage(photoKey: String, byteArray: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.openFileOutput("$photoKey.jpg", Context.MODE_PRIVATE).use { stream ->
                    stream.write(byteArray)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getByteArray(photoKey: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            val filename = photoKey.plus(".jpg")
            val files = context.filesDir.listFiles()
            files?.find { it.name == filename }?.readBytes()
        }
    }

    suspend fun getBitmap(photoKey: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val filename = photoKey.plus(".jpg")
            val files = context.filesDir.listFiles()
            val byteArray = files?.find { it.name == filename }?.readBytes()
            byteArray?.let { BitmapConverters.byteArrayToBitmap(it) }
        }
    }

    suspend fun deleteImage(photoKey: String): Boolean {
        return withContext(Dispatchers.IO) {
            val filename = photoKey.plus(".jpg")
            val files = context.filesDir.listFiles()
            files?.find { it.name == filename }?.delete() ?: true
        }
    }
}