package com.andrew.tasky.agenda.data.util

import com.andrew.tasky.agenda.data.event.EventDto
import com.andrew.tasky.agenda.data.reminder.ReminderDto
import com.andrew.tasky.agenda.data.task.TaskDto
import com.andrew.tasky.agenda.domain.models.AgendaItem
import java.time.LocalDateTime

fun TaskDto.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        isDone = isDone,
        title = title,
        description = description ?: "",
        startDateAndTime = LocalDateTime.ofEpochSecond(time, 0, null),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = time,
            remindAtEpochSecond = remindAt
        )
    )
}

fun ReminderDto.toReminder(): AgendaItem.Reminder {
    return AgendaItem.Reminder(
        id = id,
        isDone = isDone,
        title = title,
        description = description ?: "",
        startDateAndTime = LocalDateTime.ofEpochSecond(time, 0, null),
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
        startDateAndTime = LocalDateTime.ofEpochSecond(from, 0, null),
        endDateAndTime = LocalDateTime.ofEpochSecond(to, 0, null),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochSecond = from,
            remindAtEpochSecond = remindAt
        ),
        photos = photos,
        isAttendee = !isUserEventCreator,
        attendees = attendees,
        host = host
    )
}
