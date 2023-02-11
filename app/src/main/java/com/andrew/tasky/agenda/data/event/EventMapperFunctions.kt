package com.andrew.tasky.agenda.data.event

import com.andrew.tasky.agenda.data.util.ReminderTimeConversion
import com.andrew.tasky.agenda.data.util.toLocalDateTime
import com.andrew.tasky.agenda.data.util.toZonedEpochMilli
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto

fun AgendaItem.Event.toCreateEventRequest(): CreateEventRequest {
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = startDateAndTime.toZonedEpochMilli(),
        to = endDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toEpochMilli(
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
        remindAt = ReminderTimeConversion.toEpochMilli(
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
        reminderTime = ReminderTimeConversion.toEpochMilli(
            reminderTime = reminderTime,
            startLocalDateTime = startDateAndTime
        ),
        isGoing = isGoing
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
        photos = photos.map { it.toEventPhoto() },
        isGoing = isGoing
    )
}

fun AttendeeDto.toAttendee(hostId: String): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
        isCreator = hostId == userId
    )
}

fun EventPhotoDto.toEventPhoto(): EventPhoto {
    return EventPhoto.Remote(
        key = key,
        photoUrl = url
    )
}

fun EventEntity.toEvent(): AgendaItem.Event {
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
        isGoing = isGoing
    )
}
