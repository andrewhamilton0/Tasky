package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.event.GetAttendeeResponse
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface EventRepository {
    suspend fun upsertEvent(event: AgendaItem.Event): AuthResult<Unit>
    suspend fun deleteEvent(event: AgendaItem.Event)
    suspend fun getAttendee(email: String): AuthResult<GetAttendeeResponse>
    suspend fun deleteAttendee(eventId: String): AuthResult<Unit>
    suspend fun uploadCreateAndUpdateModifiedEvents()
}
