package com.andrew.tasky.agenda.data.storage

import android.content.Context
import com.andrew.tasky.agenda.data.event.photo.LocalPhotoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

class InternalStorage(private val context: Context) {
    fun saveImage(filename: String, byteArray: ByteArray): Boolean {
        return try {
            context.openFileOutput("$filename.jpg", Context.MODE_PRIVATE).use { stream ->
                stream.write(byteArray)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadImages(): List<LocalPhotoDto> {
        return withContext(Dispatchers.IO) {
            val files = context.filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                LocalPhotoDto(key = it.name, byteArray = it.readBytes())
            } ?: emptyList()
        }
    }
}