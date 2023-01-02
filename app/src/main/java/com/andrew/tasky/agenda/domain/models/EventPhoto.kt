package com.andrew.tasky.agenda.domain.models

import android.net.Uri

sealed interface EventPhoto {

    data class Remote(
        val key: String,
        val photoUrl: String,
    ) : EventPhoto

    class Local(
        val uri: Uri
    ) : EventPhoto
}