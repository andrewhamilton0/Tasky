package com.andrew.tasky.agenda.data.util

import android.content.Context
import android.net.Uri
import com.andrew.tasky.agenda.domain.UriByteConverter
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UriByteConverterImpl @Inject constructor(
    private val appContext: Context,
) : UriByteConverter {
    override suspend fun uriToByteArray(uri: Uri): ByteArray {
        return withContext(Dispatchers.IO) {
            return@withContext appContext.contentResolver.openInputStream(uri).use {
                it!!.readBytes()
            }
        }
    }
}
