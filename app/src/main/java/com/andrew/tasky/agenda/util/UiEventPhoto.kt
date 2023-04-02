package com.andrew.tasky.agenda.util

import com.andrew.tasky.agenda.domain.models.EventPhoto

sealed interface UiEventPhoto {
    data class Photo(val eventPhoto: EventPhoto) : UiEventPhoto
    object AddPhoto : UiEventPhoto
}
