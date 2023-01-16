package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.auth.data.AuthResult
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val db: EventDatabase
) : EventRepository {

    override suspend fun createEvent(): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvent(eventId: String): AuthResult<AgendaItem.Event> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAttendee(email: String): AuthResult<Attendee> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendee(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }
}
