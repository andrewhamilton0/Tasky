package com.andrew.tasky.agenda.domain

import android.net.Uri

interface UriByteConverter {
    suspend fun uriToByteArray(uri: Uri): ByteArray
}
