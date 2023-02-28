package com.andrew.tasky.agenda.util

import android.graphics.Bitmap
import com.andrew.tasky.agenda.domain.models.EventPhoto

interface UiEventPhoto {
    data class RemotePhoto(val remoteEventPhoto: EventPhoto.Remote) : UiEventPhoto
    data class LocalPhoto(val bitmap: Bitmap, val key: String? = null) : UiEventPhoto
    object AddPhoto : UiEventPhoto
}
