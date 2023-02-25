package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.core.Resource

interface EventRepository {
    suspend fun upsertEvent(event: AgendaItem.Event): Resource<Unit>
    suspend fun deleteEvent(event: AgendaItem.Event)
    suspend fun getAttendee(email: String): Resource<Attendee>
    suspend fun deleteAttendee(eventId: String): Resource<Unit>
    suspend fun uploadCreateAndUpdateModifiedEvents()
}
