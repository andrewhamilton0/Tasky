package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.agenda.data.util.ReminderTimeConversion
import com.andrew.tasky.agenda.data.util.toLocalDateTime
import com.andrew.tasky.agenda.data.util.toZonedEpochMilli
import com.andrew.tasky.agenda.domain.models.AgendaItem

fun TaskDto.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description ?: "",
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = startDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toEpochMilli(
            startLocalDateTime = startDateAndTime,
            reminderTime = reminderTime
        ),
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        time = startDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toEpochMilli(
            startLocalDateTime = startDateAndTime,
            reminderTime = reminderTime
        )
    )
}

fun TaskEntity.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = time.toLocalDateTime(),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochMilli = time,
            remindAtEpochMilli = remindAt
        )
    )
}

fun TaskEntity.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}
