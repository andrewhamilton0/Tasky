package com.andrew.tasky.agenda.data.event

import androidx.core.net.toFile
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EventRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: EventApi
) : EventRepository {

    override suspend fun createEvent(event: AgendaItem.Event) {
        getAuthResult {
            api.createEvent(
                eventData = MultipartBody.Part
                    .create(Json.encodeToString(event.toCreateEventRequest()).toRequestBody()),
                photoData = event.photos?.map { eventPhoto ->
                    when (eventPhoto) {
                        is EventPhoto.Local -> {
                            MultipartBody.Part
                                .create(eventPhoto.uri.toFile().readBytes().toRequestBody())
                        }
                        is EventPhoto.Remote -> {
                            MultipartBody.Part
                                .create(eventPhoto.photoUrl.encodeToByteArray().toRequestBody())
                        }
                    }
                } ?: emptyList()
            )
        }
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
