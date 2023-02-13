package com.andrew.tasky.agenda.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class UriByteConverter(
    val appContext: Context
) {
    fun uriToByteArray(uri: Uri, targetSize: Int): ByteArray {
        val imageByte = appContext.contentResolver.openInputStream(uri).use {
            it!!.readBytes()
        }
        val bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        var quality = 100
        var result = imageByte
        while (result.size > targetSize && quality > 0) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            result = stream.toByteArray()
            quality -= 5
        }
        return result
    }
}
