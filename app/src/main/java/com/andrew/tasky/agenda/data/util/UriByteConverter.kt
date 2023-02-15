package com.andrew.tasky.agenda.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UriByteConverter(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun uriToByteArray(uri: Uri): ByteArray {
        return withContext(ioDispatcher) {
            return@withContext appContext.contentResolver.openInputStream(uri).use {
                it!!.readBytes()
            }
        }
    }

    fun imageSizeCompressor(byteArray: ByteArray, targetSize: Int): ByteArray {
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
