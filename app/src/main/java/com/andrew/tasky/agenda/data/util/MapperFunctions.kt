package com.andrew.tasky.agenda.data.util

import com.andrew.tasky.agenda.data.event.EventDto
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun EventDto.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        id = id,
        isDone = false,
        title = title,
        description = description,
        startDateAndTime = LocalDateTime.ofEpochSecond(from, 0, ZoneOffset.UTC),
        endDateAndTime = LocalDateTime.ofEpochSecond(to, 0, ZoneOffset.UTC),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochMilli = from,
            remindAtEpochMilli = remindAt
        ),
        photos = photos.map { EventPhoto.Remote(key = it.key, photoUrl = it.url) },
        isAttendee = !isUserEventCreator,
        attendees = attendees.map {
            Attendee(
                email = it.email,
                eventId = it.eventId,
                fullName = it.fullName,
                isCreator = it.userId == host,
                isGoing = it.isGoing,
                remindAt = remindAt,
                userId = it.userId
            )
        },
        host = host
    )
}
