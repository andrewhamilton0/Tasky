package com.andrew.tasky.agenda.data.util

import com.andrew.tasky.agenda.data.event.EventDto
import com.andrew.tasky.agenda.data.reminder.ReminderDto
import com.andrew.tasky.agenda.data.reminder.ReminderEntity
import com.andrew.tasky.agenda.data.task.TaskDto
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import java.time.LocalDateTime
import java.time.ZoneOffset

fun TaskDto.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        title = title,
        description = description ?: "",
        startDateAndTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = time,
            remindAtEpochSecond = remindAt
        )
    )
}

fun ReminderDto.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        id = id,
        title = title,
        description = description ?: "",
        startDateAndTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = time,
            remindAtEpochSecond = remindAt
        )
    )
}

fun AgendaItem.Reminder.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = startDateAndTime.toEpochSecond(ZoneOffset.UTC),
        remindAt = ReminderTimeConversion.toEpochSecond(
            startTimeEpochSecond = startDateAndTime.toEpochSecond(ZoneOffset.UTC),
            reminderTime = reminderTime
        )
    )
}

fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        time = startDateAndTime.toEpochSecond(ZoneOffset.UTC),
        remindAt = ReminderTimeConversion.toEpochSecond(
            startTimeEpochSecond = startDateAndTime.toEpochSecond(ZoneOffset.UTC),
            reminderTime = reminderTime
        )
    )
}

fun ReminderEntity.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = time,
            remindAtEpochSecond = remindAt
        )
    )
}

fun EventDto.toEvent(): AgendaItem.Event {
    return AgendaItem.Event(
        id = id,
        isDone = false,
        title = title,
        description = description,
        startDateAndTime = LocalDateTime.ofEpochSecond(from, 0, ZoneOffset.UTC),
        endDateAndTime = LocalDateTime.ofEpochSecond(to, 0, ZoneOffset.UTC),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = from,
            remindAtEpochSecond = remindAt
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
