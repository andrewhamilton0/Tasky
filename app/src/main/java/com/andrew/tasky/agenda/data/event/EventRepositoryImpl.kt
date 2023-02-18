package com.andrew.tasky.agenda.data.event

import android.content.Context
import android.net.Uri
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.BitmapCompressor
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.data.util.UriByteConverter
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
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

    override suspend fun upsertEvent(event: AgendaItem.Event): AuthResult<Unit> {
        if (db.getEventDao().getEventById(event.id) == null ||
            db.getEventDao().getModifiedEventById(event.id)?.modifiedType == ModifiedType.CREATE
        ) {
            println("CREATING EVENT")
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
            return when (result) {
                is AuthResult.Authorized -> {
                    result.data?.let {
                        db.getEventDao().upsertEvent(
                            it.toEventEntity(
                                isDone = event.isDone,
                                isGoing = event.isGoing
                            )
                        )
                    }
                    AuthResult.Authorized()
                }
                is AuthResult.Unauthorized -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = event.id,
                            modifiedType = ModifiedType.CREATE
                        )
                    )
                    AuthResult.Unauthorized()
                }
                is AuthResult.UnknownError -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = event.id,
                            modifiedType = ModifiedType.CREATE
                        )
                    )
                    AuthResult.UnknownError()
                }
            }
        } else {
            db.getEventDao().upsertEvent(event.toEventEntity())
            println("UPDATING EVENT")
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
            return when (result) {
                is AuthResult.Authorized -> {
                    result.data?.let {
                        db.getEventDao().upsertEvent(
                            it.toEventEntity(
                                isDone = event.isDone,
                                isGoing = event.isGoing
                            )
                        )
                    }
                    AuthResult.Authorized()
                }
                is AuthResult.Unauthorized -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = event.id,
                            modifiedType = ModifiedType.UPDATE
                        )
                    )
                    AuthResult.Unauthorized()
                }
                is AuthResult.UnknownError -> {
                    db.getEventDao().upsertModifiedEvent(
                        ModifiedEventEntity(
                            id = event.id,
                            modifiedType = ModifiedType.UPDATE
                        )
                    )
                    AuthResult.Unauthorized()
                }
            }
        }
    }

    override suspend fun deleteEvent(event: AgendaItem.Event) {
        db.getEventDao().deleteEvent(event.toEventEntity())
        val result = getAuthResult { api.deleteEvent(event.id) }
        if (result !is AuthResult.Authorized) {
            db.getEventDao().upsertModifiedEvent(
                ModifiedEventEntity(
                    id = event.id,
                    modifiedType = ModifiedType.DELETE
                )
            )
        }
    }

    override suspend fun getAttendee(email: String): AuthResult<GetAttendeeResponse> {
        val result = getAuthResult { api.getAttendee(email) }
        when (result) {
            is AuthResult.Authorized -> return AuthResult.Authorized(result.data)
            is AuthResult.Unauthorized -> return AuthResult.Unauthorized()
            is AuthResult.UnknownError -> return AuthResult.UnknownError()
        }
    }

    override suspend fun deleteAttendee(eventId: String): AuthResult<Unit> {
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
                if (results is AuthResult.Authorized) {
                    db.getEventDao().deleteModifiedEventById(event.id)
                }
            }
        }
    }
}
