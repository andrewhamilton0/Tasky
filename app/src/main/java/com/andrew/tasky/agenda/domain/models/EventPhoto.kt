package com.andrew.tasky.agenda.domain.models

import android.net.Uri
import java.util.UUID

sealed class EventPhoto(open val key: String) {

    data class Remote(
        override val key: String = UUID.randomUUID().toString(),
        val photoUrl: String,
    ) : EventPhoto(key = key)

    data class Local(
        override val key: String = UUID.randomUUID().toString(),
        val uri: Uri
    ) : EventPhoto(key = key)
}
