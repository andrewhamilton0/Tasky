package com.andrew.tasky.agenda.data.event

import android.content.Context
import android.net.Uri
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.BitmapCompressor
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.data.util.UriByteConverter
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EventRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: EventApi,
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher
) : EventRepository {

    override suspend fun createEvent(event: AgendaItem.Event) {
        db.getEventDao().upsertEvent(event.toEventEntity())
        val result = getAuthResult {
            api.createEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "create_event_request",
                        value = Json.encodeToString(event.toCreateEventRequest())
                    ),
                photoData = event.photos.filterIsInstance<EventPhoto.Local>().mapIndexed {
                    index, eventPhoto ->
                    val uriByteConverter = UriByteConverter(
                        appContext = appContext,
                        ioDispatcher = ioDispatcher
                    )
                    val imageByte = uriByteConverter.uriToByteArray(
                        uri = Uri.parse(eventPhoto.uri),
                    )

                    val compressedImageByte = BitmapCompressor().compressByteArray(
                        byteArray = imageByte,
                        targetSize = 1000000
                    )
                    MultipartBody.Part
                        .createFormData(
                            name = "photo$index",
                            filename = eventPhoto.key,
                            body = compressedImageByte.toRequestBody()
                        )
                }
            )
        }
        when (result) {
            is AuthResult.Authorized -> {
                result.data?.let {
                    db.getEventDao().upsertEvent(
                        it.toEventEntity(
                            isDone = event.isDone,
                            isGoing = event.isGoing
                        )
                    )
                }
            }
            else -> db.getEventDao().upsertModifiedEvent(
                ModifiedEventEntity(
                    id = event.id,
                    modifiedType = ModifiedType.CREATE
                )
            )
        }
    }

    override suspend fun getEvent(eventId: String): AuthResult<AgendaItem.Event> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(event: AgendaItem.Event) {
        db.getEventDao().upsertEvent(event.toEventEntity())
        val result = getAuthResult {
            api.updateEvent(
                eventData = MultipartBody.Part
                    .createFormData(
                        name = "update_event_request",
                        value = Json.encodeToString(event.toUpdateEventRequest())
                    ),
                photoData = event.photos.filterIsInstance<EventPhoto.Local>().mapIndexed {
                    index, eventPhoto ->
                    val uriByteConverter = UriByteConverter(
                        appContext = appContext,
                        ioDispatcher = ioDispatcher
                    )
                    val imageByte = uriByteConverter.uriToByteArray(
                        uri = Uri.parse(eventPhoto.uri),
                    )
                    val compressedImageByte = BitmapCompressor().compressByteArray(
                        byteArray = imageByte,
                        targetSize = 1000000
                    )
                    MultipartBody.Part
                        .createFormData(
                            name = "photo$index",
                            filename = eventPhoto.key,
                            body = compressedImageByte.toRequestBody()
                        )
                }
            )
        }
        when (result) {
            is AuthResult.Authorized -> {
                result.data?.let {
                    db.getEventDao().upsertEvent(
                        it.toEventEntity(
                            isDone = event.isDone,
                            isGoing = event.isGoing
                        )
                    )
                }
            }
            else -> db.getEventDao().upsertModifiedEvent(
                ModifiedEventEntity(
                    id = event.id,
                    modifiedType = ModifiedType.UPDATE
                )
            )
        }
    }

    override suspend fun getAttendee(email: String): AuthResult<Attendee> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendee(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadCreateAndUpdateModifiedEvents() {
        val modifiedEvents = db.getEventDao().getModifiedEvents().groupBy {
            it.modifiedType
        }

        val createEvents = modifiedEvents[ModifiedType.CREATE]?.map {
            db.getEventDao().getEventById(it.id)?.toEvent()
        }
        createEvents?.forEach { event ->
            event?.let {
                createEvent(event)
                db.getEventDao().deleteModifiedEventById(event.id)
            }
        }

        val updateEvents = modifiedEvents[ModifiedType.UPDATE]?.map {
            db.getEventDao().getEventById(it.id)?.toEvent()
        }
        updateEvents?.forEach { event ->
            event?.let {
                updateEvent(event)
                db.getEventDao().deleteModifiedEventById(event.id)
            }
        }
    }
}
