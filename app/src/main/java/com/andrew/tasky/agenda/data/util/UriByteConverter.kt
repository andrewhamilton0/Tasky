package com.andrew.tasky.agenda.data.util

import android.content.Context
import android.net.Uri
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
}
