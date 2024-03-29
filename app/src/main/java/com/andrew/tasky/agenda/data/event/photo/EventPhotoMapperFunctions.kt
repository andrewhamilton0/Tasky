package com.andrew.tasky.agenda.data.event.photo

import com.andrew.tasky.agenda.data.networkmodels.RemoteEventPhotoDto
import com.andrew.tasky.agenda.domain.models.EventPhoto

fun RemoteEventPhotoDto.toEventPhoto(): EventPhoto.Remote {
    return EventPhoto.Remote(
        key = key,
        photoUrl = url
    )
}

fun EventPhoto.Remote.toRemotePhotoDto(): RemoteEventPhotoDto {
    return RemoteEventPhotoDto(
        key = key,
        url = photoUrl
    )
}
