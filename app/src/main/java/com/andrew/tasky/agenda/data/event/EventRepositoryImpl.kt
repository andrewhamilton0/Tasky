package com.andrew.tasky.agenda.data.event

import android.content.Context
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.storage.ImageStorage
import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.domain.models.UpsertEventResult
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.Resource
import com.andrew.tasky.core.UiText
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

typealias LocalPhoto = EventPhoto.Local

class EventRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: EventApi,
    private val context: Context,
) : EventRepository {

    private val imageStorage = ImageStorage(context)
    private val bitmapConverter = BitmapConverters

    override suspend fun upsertEvent(event: AgendaItem.Event): UpsertEventResult {

        val (compressedEvent, photosDeleted) = compressEvent(event)

        val isEventInDb = db.getEventDao().getEventById(compressedEvent.id) == null
        val isModifiedCreateEvent = db.getEventDao().getModifiedEventById(compressedEvent.id)
            ?.modifiedType == ModifiedType.CREATE

        db.getEventDao().upsertEvent(compressedEvent.toEventEntity())

        if (isEventInDb || isModifiedCreateEvent) {
            val result = getResourceResult {
                api.createEvent(
                    eventData = MultipartBody.Part
                        .createFormData(
                            name = "create_event_request",
                            value = Json.encodeToString(compressedEvent.toCreateEventRequest())
                        ),
                    photoData = compressedEvent.photos.filterIsInstance<LocalPhoto>()
                        .mapIndexedNotNull {
                            index, eventPhoto ->
                            MultipartBody.Part
                                .createFormData(
                                    name = "photo$index",
                                    filename = eventPhoto.key,
                                    body = eventPhoto.byteArray?.toRequestBody()
                                        ?: return@mapIndexedNotNull null
                                )
                        }
                )
            }
            return when (result) {
                is Resource.Error -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = compressedEvent.id,
                            modifiedType = ModifiedType.CREATE
                        )
                    )
                    compressedEvent.photos.filterIsInstance<LocalPhoto>().forEach {
                        saveLocalPhotoInternally(it.copy(savedInternally = true))
                    }
                    UpsertEventResult(deletedPhotos = photosDeleted, resource = Resource.Error())
                }
                is Resource.Success -> {
                    result.data?.let {
                        db.getEventDao().upsertEvent(
                            it.toEventEntity(
                                isDone = compressedEvent.isDone,
                                isGoing = compressedEvent.isGoing
                            )
                        )
                    }
                    UpsertEventResult(deletedPhotos = photosDeleted, resource = Resource.Success())
                }
            }
        } else {
            val result = getResourceResult {
                api.updateEvent(
                    eventData = MultipartBody.Part
                        .createFormData(
                            name = "update_event_request",
                            value = Json.encodeToString(compressedEvent.toUpdateEventRequest())
                        ),
                    photoData = compressedEvent.photos.filterIsInstance<LocalPhoto>().mapIndexed {
                        index, eventPhoto ->
                        MultipartBody.Part
                            .createFormData(
                                name = "photo$index",
                                filename = eventPhoto.key,
                                body = eventPhoto.byteArray!!.toRequestBody()
                            )
                    }
                )
            }
            return when (result) {
                is Resource.Error -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = compressedEvent.id,
                            modifiedType = ModifiedType.UPDATE
                        )
                    )
                    compressedEvent.photos.filterIsInstance<LocalPhoto>().filter {
                        !it.savedInternally
                    }.forEach {
                        saveLocalPhotoInternally(it.copy(savedInternally = true))
                    }
                    UpsertEventResult(deletedPhotos = photosDeleted, resource = Resource.Error())
                }
                is Resource.Success -> {
                    result.data?.let {
                        db.getEventDao().upsertEvent(
                            it.toEventEntity(
                                isDone = compressedEvent.isDone,
                                isGoing = compressedEvent.isGoing
                            )
                        )
                    }
                    compressedEvent.photos.filterIsInstance<LocalPhoto>().filter {
                        it.savedInternally
                    }.forEach {
                        imageStorage.deleteImage(it.key)
                    }
                    UpsertEventResult(deletedPhotos = photosDeleted, resource = Resource.Success())
                }
            }
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        db.getEventDao().deleteEvent(eventId)
        val result = getResourceResult { api.deleteEvent(eventId) }
        if (result is Resource.Error) {
            db.getEventDao().upsertModifiedEvent(
                ModifiedEventEntity(
                    id = eventId,
                    modifiedType = ModifiedType.DELETE
                )
            )
        }
    }

    override suspend fun getEvent(eventId: String): AgendaItem.Event? {
        return db.getEventDao().getEventById(eventId)?.toEvent(context)
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
            db.getEventDao().getEventById(it.id)?.toEvent(context)
        }
        createAndUpdateModifiedEvents.forEach { event ->
            event?.let {
                val (results) = upsertEvent(event)
                if (results is Resource.Success) {
                    db.getEventDao().deleteModifiedEventById(event.id)
                }
            }
        }
    }

    private data class CompressEventResult(val event: AgendaItem.Event, val photosDeleted: Int)
    private suspend fun compressEvent(event: AgendaItem.Event): CompressEventResult {
        return withContext(Dispatchers.Default) {
            var photosDeleted = 0
            val compressedEvent = event.copy(
                photos = supervisorScope {
                    event.photos
                        .chunked(MAX_PARALLEL_IMAGE_COMPRESS_COUNT)
                        .map { eventPhotoChunk ->
                            eventPhotoChunk.map { eventPhoto ->
                                async {
                                    if (eventPhoto is LocalPhoto) {
                                        if (eventPhoto.savedInternally) {
                                            val byteArray =
                                                imageStorage.getByteArray(eventPhoto.key)
                                            eventPhoto.copy(byteArray = byteArray)
                                        } else {
                                            val byteArray = eventPhoto.bitmap?.let { bitmap ->
                                                bitmapConverter.bitmapToCompressByteArray(
                                                    bitmap, MAX_PHOTO_SIZE_IN_BYTES
                                                )
                                            }
                                            if (byteArray != null) {
                                                eventPhoto.copy(byteArray = byteArray)
                                            } else {
                                                photosDeleted++
                                                null
                                            }
                                        }
                                    } else {
                                        eventPhoto
                                    }
                                }
                            }.mapNotNull { it.await() }
                        }.flatten()
                }
            )
            CompressEventResult(event = compressedEvent, photosDeleted = photosDeleted)
        }
    }

    private suspend fun saveLocalPhotoInternally(photo: LocalPhoto) {
        photo.byteArray?.let {
            ImageStorage(context = context).saveImage(
                photoKey = photo.key,
                byteArray = it
            )
        }
    }

    companion object {
        const val MAX_PHOTO_SIZE_IN_BYTES = 1_000_000
        private const val MAX_PARALLEL_IMAGE_COMPRESS_COUNT = 3
    }
}
