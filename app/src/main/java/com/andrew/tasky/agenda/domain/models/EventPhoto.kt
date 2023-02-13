package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import java.util.UUID
import kotlinx.parcelize.Parcelize

sealed class EventPhoto(open val key: String) {

    @Parcelize
    data class Remote(
        override val key: String = UUID.randomUUID().toString(),
        val photoUrl: String,
    ) : EventPhoto(key = key), Parcelable

    @Parcelize
    data class Local(
        override val key: String = UUID.randomUUID().toString(),
        val uri: String
    ) : EventPhoto(key = key), Parcelable
}
