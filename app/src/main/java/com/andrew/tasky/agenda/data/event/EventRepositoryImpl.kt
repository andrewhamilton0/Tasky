package com.andrew.tasky.agenda.data.event

import android.content.Context
import android.util.Log
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.event.photo.LocalPhotoDto
import com.andrew.tasky.agenda.data.storage.InternalStorage
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.Resource
import com.andrew.tasky.core.UiText
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException

class EventRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: EventApi,
    private val context: Context
) : EventRepository {

    override suspend fun upsertEvent(event: AgendaItem.Event): Resource<Unit> {

        db.getEventDao().upsertEvent(event.toEventEntity())
        return Resource.Success()
    }

    override suspend fun deleteEvent(event: AgendaItem.Event) {
        db.getEventDao().deleteEvent(event.toEventEntity())
        val result = getResourceResult { api.deleteEvent(event.id) }
        if (result is Resource.Error) {
            db.getEventDao().upsertModifiedEvent(
                ModifiedEventEntity(
                    id = event.id,
                    modifiedType = ModifiedType.DELETE
                )
            )
        }
    }

    override suspend fun getAttendee(email: String): Resource<Attendee> {
        val result = getResourceResult { api.getAttendee(email) }
        when (result) {
            is Resource.Success -> {
                return if (result.data?.doesUserExist == true) {
                    Resource.Success(result.data.attendee)
                } else {
                    return Resource.Error(
                        errorMessage = UiText.Resource(R.string.user_not_found)
                    )
                }
            }
            is Resource.Error -> return Resource.Error(errorMessage = result.message)
        }
    }

    override suspend fun deleteAttendee(eventId: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadCreateAndUpdateModifiedEvents() {

        val createAndUpdateModifiedEvents = db.getEventDao().getModifiedEvents().filter {
            it.modifiedType == ModifiedType.CREATE || it.modifiedType == ModifiedType.UPDATE
        }.map {
            db.getEventDao().getEventById(it.id)?.toEvent()
        }
        createAndUpdateModifiedEvents.forEach { event ->
            event?.let {
                val results = upsertEvent(event)
                if (results is Resource.Success) {
                    db.getEventDao().deleteModifiedEventById(event.id)
                }
            }
        }
    }

    override suspend fun getLocalPhotos(keys: List<String>): List<LocalPhotoDto> {
        val keysWithJpgEnding = keys.map { it.plus(".jpg") }
        return InternalStorage(context = context).loadImages().filter {
            keysWithJpgEnding.contains(it.key)
        }
    }

    override suspend fun saveLocalPhoto(photo: LocalPhotoDto) {
        InternalStorage(context = context).saveImage(
            filename = photo.key,
            byteArray = photo.byteArray
        )
    }
}
