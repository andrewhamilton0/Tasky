package com.andrew.tasky.agenda.data.event

import android.content.Context
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.storage.ImageStorage
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
typealias Event = AgendaItem.Event

class EventRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: EventApi,
    private val context: Context,
) : EventRepository {

    private val imageStorage = ImageStorage(context)
    private val eventPhotoCompressor = EventPhotoCompressor()

    override suspend fun upsertEvent(event: Event): UpsertEventResult {
        val (newEvent, deletedPhotoCount) = compressAndDeletePhotosFromEvent(event)

        val isCreateEvent = isEventToBeCreated(newEvent.id)
        if (isCreateEvent) {
            val result = createRemoteEvent(newEvent)
            return when (result) {
                is Resource.Error -> {
                    createRemoteEventError(newEvent)
                    UpsertEventResult(
                        deletedPhotos = deletedPhotoCount, resource = Resource.Error()
                    )
                }
                is Resource.Success -> {
                    createRemoteEventSuccess(result, newEvent)
                    UpsertEventResult(
                        deletedPhotos = deletedPhotoCount, resource = Resource.Success()
                    )
                }
            }
        } else {
            val result = updateRemoteEvent(newEvent)
            return when (result) {
                is Resource.Error -> {
                    updateRemoteEventError(newEvent)
                    UpsertEventResult(
                        deletedPhotos = deletedPhotoCount, resource = Resource.Error()
                    )
                }
                is Resource.Success -> {
                    updateRemoteEventSuccess(result, newEvent)
                    UpsertEventResult(
                        deletedPhotos = deletedPhotoCount, resource = Resource.Success()
                    )
                }
            }
        }
    }

    data class CompressionResult(val event: Event, val deletedPhotoCount: Int)
    private suspend fun compressAndDeletePhotosFromEvent(
        event: Event
    ): CompressionResult {
        val eventPhotosWithByteArrays = photosWithSavedByteArrays(event.photos)
        val (compressedPhotos, deletedPhotoCount, deletedPhotos) = eventPhotoCompressor
            .compressPhotos(
                photos = eventPhotosWithByteArrays,
                compressionSize = MAX_PHOTO_SIZE_IN_BYTES
            )
        val newEvent = event.copy(photos = compressedPhotos)
        val internalPhotosToDelete = deletedPhotos.filter { it.savedInternally }
        if (internalPhotosToDelete.isNotEmpty()) {
            deleteInternalPhotos(internalPhotosToDelete)
            db.getEventDao().upsertEvent(event.toEventEntity())
        }
        return CompressionResult(event = newEvent, deletedPhotoCount = deletedPhotoCount)
    }

    private suspend fun deleteInternalPhotos(deletedPhotos: List<EventPhoto.Local>) {
        deletedPhotos.forEach { photo ->
            if (photo.savedInternally) imageStorage.deleteImage(photo.key)
        }
    }

    private suspend fun updateRemoteEventError(
        event: Event
    ) {
        val updatedPhotos = event.photos.map { photo ->
            if (photo is LocalPhoto && !photo.savedInternally) {
                saveLocalPhotoInternally(photo)
                photo.copy(savedInternally = true)
            } else photo
        }
        val updatedEvent = event.copy(photos = updatedPhotos)
        db.getEventDao().upsertEvent(updatedEvent.toEventEntity())
        db.getEventDao().upsertModifiedEvent(
            ModifiedEventEntity(
                id = updatedEvent.id,
                modifiedType = ModifiedType.UPDATE
            )
        )
    }

    private suspend fun updateRemoteEventSuccess(
        result: Resource<EventDto>,
        event: Event
    ) {
        upsertApiResultToDb(result, event)
        event.photos.filterIsInstance<LocalPhoto>().filter {
            it.savedInternally
        }.forEach {
            imageStorage.deleteImage(it.key)
        }
    }

    private suspend fun createRemoteEventSuccess(
        result: Resource<EventDto>,
        newEvent: Event
    ) {
        upsertApiResultToDb(result, newEvent)
    }

    private suspend fun upsertApiResultToDb(
        result: Resource<EventDto>,
        event: Event
    ) {
        result.data?.let {
            db.getEventDao().upsertEvent(
                it.toEventEntity(
                    isDone = event.isDone,
                    isGoing = event.isGoing
                )
            )
        }
    }

    private suspend fun createRemoteEventError(
        newEvent: Event
    ) {
        db.getEventDao().upsertEvent(newEvent.toEventEntity())
        db.getEventDao().upsertModifiedEvent(
            ModifiedEventEntity(
                id = newEvent.id,
                modifiedType = ModifiedType.CREATE
            )
        )
        newEvent.photos.filterIsInstance<LocalPhoto>().forEach {
            saveLocalPhotoInternally(it.copy(savedInternally = true))
        }
    }

    private suspend fun updateRemoteEvent(event: Event): Resource<EventDto> {
        val result = getResourceResult {
            api.updateEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "update_event_request",
                        value = Json.encodeToString(event.toUpdateEventRequest())
                    ),
                photoData = event.photos.filterIsInstance<LocalPhoto>()
                    .mapIndexedNotNull { index, eventPhoto ->
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
        return result
    }

    private suspend fun createRemoteEvent(newEvent: Event): Resource<EventDto> {
        val result = getResourceResult {
            api.createEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "create_event_request",
                        value = Json.encodeToString(newEvent.toCreateEventRequest())
                    ),
                photoData = newEvent.photos.filterIsInstance<LocalPhoto>()
                    .mapIndexedNotNull { index, eventPhoto ->
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
        return result
    }

    private suspend fun photosWithSavedByteArrays(photos: List<EventPhoto>): List<EventPhoto> {
        val eventPhotosWithByteArrays = photos.map { photo ->
            if (photo is LocalPhoto && photo.savedInternally) {
                photo.copy(byteArray = imageStorage.getByteArray(photo.key))
            } else photo
        }
        return eventPhotosWithByteArrays
    }

    private suspend fun isEventToBeCreated(eventId: String): Boolean {
        val eventIsNotInDb = db.getEventDao().getEventById(eventId) == null
        val isModifiedCreateEvent = db.getEventDao().getModifiedEventById(eventId)
            ?.modifiedType == ModifiedType.CREATE
        return eventIsNotInDb || isModifiedCreateEvent
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

    override suspend fun getEvent(eventId: String): Event? {
        return db.getEventDao().getEventById(eventId)?.toEvent(this)
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
        return getResourceResult {
            api.deleteAttendee(eventId)
        }
    }

    override suspend fun uploadCreateAndUpdateModifiedEvents() {

        val createAndUpdateModifiedEvents = db.getEventDao().getModifiedEvents().filter {
            it.modifiedType == ModifiedType.CREATE || it.modifiedType == ModifiedType.UPDATE
        }.map {
            db.getEventDao().getEventById(it.id)?.toEvent(this)
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

    override suspend fun getLocalPhotos(localPhotoKeys: List<String>): List<EventPhoto.Local> {
        return withContext(Dispatchers.IO) {
            supervisorScope {
                async {
                    localPhotoKeys.map { key ->
                        val bitmap = imageStorage.getBitmap(key)
                        EventPhoto.Local(key = key, bitmap = bitmap, savedInternally = true)
                    }
                }
            }.await()
        }
    }

    private suspend fun saveLocalPhotoInternally(photo: LocalPhoto) {
        photo.byteArray?.let {
            imageStorage.saveImage(
                photoKey = photo.key,
                byteArray = it
            )
        }
    }

    companion object {
        private const val MAX_PHOTO_SIZE_IN_BYTES = 1_000_000
    }
}
