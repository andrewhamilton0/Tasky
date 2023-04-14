package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.event.attendee.toAttendee
import com.andrew.tasky.agenda.data.event.photo.toEventPhoto
import com.andrew.tasky.agenda.data.event.photo.toRemotePhotoDto
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.domain.toLocalDateTime
import com.andrew.tasky.agenda.domain.toZonedEpochMilli
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationService

fun AgendaItem.Event.toCreateEventRequest(): CreateEventRequest {
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = startDateAndTime.toZonedEpochMilli(),
        to = endDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime
        ),
        attendeeIds = attendees.map {
            it.userId
        }
    )
}

fun AgendaItem.Event.toUpdateEventRequest(): UpdateEventRequest {
    return UpdateEventRequest(
        id = id,
        title = title,
        description = description,
        from = startDateAndTime.toZonedEpochMilli(),
        to = endDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime
        ),
        attendeeIds = attendees.map {
            it.userId
        },
        deletedPhotoKeys = deletedPhotoKeys,
        isGoing = isGoing
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = startDateAndTime.toZonedEpochMilli(),
        endDateAndTime = endDateAndTime.toZonedEpochMilli(),
        host = host,
        isCreator = isCreator,
        reminderTime = ReminderTimeConversion.toZonedEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime
        ),
        isGoing = isGoing,
        remotePhotos = photos.filterIsInstance<EventPhoto.Remote>().map { it.toRemotePhotoDto() },
        localPhotosKeys = photos.filterIsInstance<EventPhoto.Local>().map { it.key },
        attendees = attendees
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

suspend fun EventEntity.toEvent(eventRepository: EventRepository): AgendaItem.Event {
    val localPhotos = eventRepository.getLocalPhotos(localPhotosKeys)
    return AgendaItem.Event(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = startDateAndTime.toLocalDateTime(),
        endDateAndTime = endDateAndTime.toLocalDateTime(),
        reminderTime = ReminderTimeConversion.toEnum(
            remindAtEpochMilli = reminderTime,
            startTimeEpochMilli = startDateAndTime
        ),
        host = host,
        isCreator = isCreator,
        isGoing = isGoing,
        photos = remotePhotos.map { it.toEventPhoto() } + localPhotos,
        attendees = attendees
    )
}

fun AgendaItem.Event.toNotificationInfo(): AgendaNotificationInfo {
    return AgendaNotificationInfo(
        title = title,
        description = description,
        id = id,
        notificationChannel = AgendaNotificationService.EVENT_CHANNEL_ID,
        navDestinationId = R.id.event_nav,
        notificationZonedMilliTime = ReminderTimeConversion.toZonedEpochMilli(
            startDateAndTime,
            reminderTime
        )
    )
}
