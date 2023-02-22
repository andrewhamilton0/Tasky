package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.event.attendee.GetAttendeeResponse
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.core.Resource

interface EventRepository {
    suspend fun upsertEvent(event: AgendaItem.Event): Resource<Unit>
    suspend fun deleteEvent(event: AgendaItem.Event)
    suspend fun getAttendee(email: String): Resource<GetAttendeeResponse>
    suspend fun deleteAttendee(eventId: String): Resource<Unit>
    suspend fun uploadCreateAndUpdateModifiedEvents()
}
