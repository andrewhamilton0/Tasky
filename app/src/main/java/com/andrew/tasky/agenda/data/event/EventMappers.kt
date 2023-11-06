package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.event.attendee.toAttendee
import com.andrew.tasky.agenda.data.event.photo.toEventPhoto
import com.andrew.tasky.agenda.data.event.photo.toRemotePhotoDto
import com.andrew.tasky.agenda.data.networkmodels.CreateEventRequest
import com.andrew.tasky.agenda.data.networkmodels.EventDto
import com.andrew.tasky.agenda.data.networkmodels.UpdateEventRequest
import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.EventPhoto

fun AgendaItem.Event.toCreateEventRequest(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): CreateEventRequest {
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = dateTimeConversion.localDateTimeToZonedEpochMilli(startDateAndTime),
        to = dateTimeConversion.localDateTimeToZonedEpochMilli(endDateAndTime),
        remindAt = reminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime,
            dateTimeConversion = dateTimeConversion
        ),
        attendeeIds = attendees.map {
            it.userId
        }
    )
}

fun AgendaItem.Event.toUpdateEventRequest(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): UpdateEventRequest {
    return UpdateEventRequest(
        id = id,
        title = title,
        description = description,
        from = dateTimeConversion.localDateTimeToZonedEpochMilli(startDateAndTime),
        to = dateTimeConversion.localDateTimeToZonedEpochMilli(endDateAndTime),
        remindAt = reminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime,
            dateTimeConversion = dateTimeConversion
        ),
        attendeeIds = attendees.map {
            it.userId
        },
        deletedPhotoKeys = deletedPhotos.filterIsInstance<EventPhoto.Remote>().map { it.key },
        isGoing = isGoing
    )
}

fun AgendaItem.Event.toEventEntity(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): EventEntity {
    return EventEntity(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = dateTimeConversion.localDateTimeToZonedEpochMilli(startDateAndTime),
        endDateAndTime = dateTimeConversion.localDateTimeToZonedEpochMilli(endDateAndTime),
        host = host,
        isCreator = isCreator,
        reminderTime = reminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime,
            dateTimeConversion = dateTimeConversion
        ),
        isGoing = isGoing,
        remotePhotos = photos.filterIsInstance<EventPhoto.Remote>().map { it.toRemotePhotoDto() },
        localPhotosKeys = photos.filterIsInstance<EventPhoto.Local>().map { it.key },
        attendees = attendees,
        remoteDeletedPhotos = deletedPhotos.filterIsInstance<EventPhoto.Remote>().map {
            it.toRemotePhotoDto()
        }
    )
}

fun EventDto.toEventEntity(isDone: Boolean, isGoing: Boolean): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        isDone = isDone,
        startDateAndTime = from,
        endDateAndTime = to,
        reminderTime = remindAt,
        host = host,
        isCreator = isUserEventCreator,
        attendees = attendees.map { it.toAttendee(hostId = host) },
        remotePhotos = photos,
        localPhotosKeys = emptyList(),
        isGoing = isGoing
    )
}

suspend fun EventEntity.toEvent(
    eventRepository: EventRepository,
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): AgendaItem.Event {
    val localPhotos = eventRepository.getLocalPhotos(localPhotosKeys)
    return AgendaItem.Event(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = dateTimeConversion.zonedEpochMilliToLocalDateTime(startDateAndTime),
        endDateAndTime = dateTimeConversion.zonedEpochMilliToLocalDateTime(endDateAndTime),
        reminderTime = reminderTimeConversion.toEnum(
            remindAtEpochMilli = reminderTime,
            startTimeEpochMilli = startDateAndTime,
            dateTimeConversion = dateTimeConversion
        ),
        host = host,
        isCreator = isCreator,
        isGoing = isGoing,
        photos = remotePhotos.map { it.toEventPhoto() } + localPhotos,
        attendees = attendees,
        deletedPhotos = remoteDeletedPhotos.map { it.toEventPhoto() }
    )
}
