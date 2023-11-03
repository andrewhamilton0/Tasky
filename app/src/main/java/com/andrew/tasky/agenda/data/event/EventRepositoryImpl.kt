package com.andrew.tasky.agenda.data.event

import android.content.Context
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.event.attendee.toAttendee
import com.andrew.tasky.agenda.data.networkmodels.EventDto
import com.andrew.tasky.agenda.data.storage.ImageStorage
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler
import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.domain.models.UpsertEventResult
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.UiText
import com.andrew.tasky.core.data.Resource
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
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
    private val scheduler: AgendaNotificationScheduler,
    private val dateTimeConversion: DateTimeConversion,
    private val reminderTimeConversion: ReminderTimeConversion
) : EventRepository {

    private val imageStorage = ImageStorage(context)
    private val eventPhotoCompressor = EventPhotoCompressor()

    override suspend fun upsertEvent(event: Event): UpsertEventResult {
        val unsavedLocalPhotos = event.photos.filterIsInstance<LocalPhoto>().filter {
            !it.savedInternally
        }
        val (compressedPhotos, deletedPhotoCount) = compressAndDeletePhotos(unsavedLocalPhotos)

        val eventWithCompressedPhotos = event.copy(
            photos = event.photos.filterNot { photo ->
                photo is LocalPhoto && !photo.savedInternally
            }.toMutableList().apply {
                compressedPhotos.forEach { compressedPhoto ->
                    this.add(compressedPhoto)
                }
            }.toList()
        )
        val newEvent = eventWithCompressedPhotos.copy(
            photos = getPhotosSavedByteArrays(eventWithCompressedPhotos.photos)
        )

        scheduleNotification(newEvent)

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
            deleteInternalPhotos(newEvent.deletedPhotos.filterIsInstance<LocalPhoto>())
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

    data class CompressionResult(val photos: List<LocalPhoto>, val deletedPhotoCount: Int)
    private suspend fun compressAndDeletePhotos(
        photos: List<LocalPhoto>
    ): CompressionResult {
        val (compressedPhotos, deletedPhotoCount) = eventPhotoCompressor
            .compressPhotos(
                photos = photos,
                compressionSize = MAX_PHOTO_SIZE_IN_BYTES
            )
        return CompressionResult(photos = compressedPhotos, deletedPhotoCount = deletedPhotoCount)
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
        db.getEventDao().upsertEvent(
            updatedEvent.toEventEntity(
                dateTimeConversion = dateTimeConversion,
                reminderTimeConversion = reminderTimeConversion
            )
        )
        db.getEventDao().upsertModifiedEvent(
            ModifiedEventEntity(
                id = updatedEvent.id,
                modifiedType = ModifiedType.UPDATE
            )
        )
    }

    private suspend fun updateRemoteEventSuccess(
        result: Resource<EventDto>,
        localEvent: Event
    ) {
        upsertApiResultToDb(result, localEvent)
        localEvent.photos.filterIsInstance<LocalPhoto>().filter {
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
        localEvent: Event
    ) {
        result.data?.let {
            db.getEventDao().upsertEvent(
                it.toEventEntity(
                    isDone = localEvent.isDone,
                    isGoing = localEvent.isGoing
                )
            )
        }
    }

    private suspend fun createRemoteEventError(
        newEvent: Event
    ) {
        db.getEventDao().upsertEvent(
            newEvent.toEventEntity(
                dateTimeConversion = dateTimeConversion,
                reminderTimeConversion = reminderTimeConversion
            )
        )
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
        return getResourceResult {
            api.updateEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "update_event_request",
                        value = Json.encodeToString(
                            event.toUpdateEventRequest(
                                dateTimeConversion = dateTimeConversion,
                                reminderTimeConversion = reminderTimeConversion
                            )
                        )
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
    }

    private suspend fun createRemoteEvent(newEvent: Event): Resource<EventDto> {
        return getResourceResult {
            api.createEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "create_event_request",
                        value = Json.encodeToString(
                            newEvent.toCreateEventRequest(
                                dateTimeConversion = dateTimeConversion,
                                reminderTimeConversion = reminderTimeConversion
                            )
                        )
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
    }

    private suspend fun getPhotosSavedByteArrays(photos: List<EventPhoto>): List<EventPhoto> {
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
        cancelScheduledNotification(eventId)
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
        return db.getEventDao().getEventById(eventId)?.toEvent(
            eventRepository = this,
            dateTimeConversion = dateTimeConversion,
            reminderTimeConversion = reminderTimeConversion
        )
    }

    override suspend fun getAttendee(email: String): Resource<Attendee> {
        val result = getResourceResult { api.getAttendee(email) }
        when (result) {
            is Resource.Success -> {
                return if (result.data?.doesUserExist == true) {
                    Resource.Success(result.data.attendee.toAttendee())
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

        val createAndUpdateModifiedEvents = db.getEventDao().getModifiedEvents().first().filter {
            it.modifiedType == ModifiedType.CREATE || it.modifiedType == ModifiedType.UPDATE
        }.map {
            db.getEventDao().getEventById(it.id)?.toEvent(
                eventRepository = this,
                dateTimeConversion = dateTimeConversion,
                reminderTimeConversion = reminderTimeConversion
            )
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
                localPhotoKeys.map { key ->
                    async {
                        val bitmap = imageStorage.getBitmap(key)
                        EventPhoto.Local(key = key, bitmap = bitmap, savedInternally = true)
                    }
                }.map { it.await() }
            }
        }
    }

    private fun scheduleNotification(event: Event) {
        scheduler.schedule(
            agendaId = event.id,
            time = reminderTimeConversion.toZonedEpochMilli(
                startLocalDateTime = event.startDateAndTime,
                reminderTime = event.reminderTime,
                dateTimeConversion = dateTimeConversion
            )
        )
    }

    private fun cancelScheduledNotification(eventId: String) {
        scheduler.cancel(eventId)
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
