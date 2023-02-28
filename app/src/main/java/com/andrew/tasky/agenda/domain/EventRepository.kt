package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.event.photo.LocalPhotoDto
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.core.Resource

interface EventRepository {
    suspend fun upsertEvent(event: AgendaItem.Event): Resource<Unit>
    suspend fun deleteEvent(event: AgendaItem.Event)
    suspend fun getAttendee(email: String): Resource<Attendee>
    suspend fun deleteAttendee(eventId: String): Resource<Unit>
    suspend fun uploadCreateAndUpdateModifiedEvents()
    suspend fun getLocalPhotos(keys: List<String>): List<LocalPhotoDto>
    suspend fun saveLocalPhoto(photo: LocalPhotoDto)
}
