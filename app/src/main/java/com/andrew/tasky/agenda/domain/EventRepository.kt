package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.auth.data.AuthResult

interface EventRepository {
    suspend fun createEvent(): AuthResult<Unit> // TODO
    suspend fun getEvent(eventId: String): AuthResult<AgendaItem>
    suspend fun deleteEvent(eventId: String): AuthResult<Unit>
    suspend fun updateEvent(): AuthResult<Unit> // TODO
    suspend fun getAttendee(email: String): AuthResult<Attendee>
    suspend fun deleteAttendee(eventId: String): AuthResult<Unit>
}
