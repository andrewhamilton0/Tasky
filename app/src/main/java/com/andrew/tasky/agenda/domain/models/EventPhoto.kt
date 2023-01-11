package com.andrew.tasky.agenda.domain.models

import android.net.Uri
import java.util.UUID

sealed class EventPhoto(val key: String = UUID.randomUUID().toString()) {

    data class Remote(
        val photoUrl: String,
    ) : EventPhoto()

    data class Local(
        val uri: Uri
    ) : EventPhoto()
}
