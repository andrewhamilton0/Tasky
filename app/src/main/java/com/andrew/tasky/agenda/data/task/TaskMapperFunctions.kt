package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
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

fun AgendaItem.Task.toTaskDto(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = dateTimeConversion.localDateTimeToZonedEpochMilli(startDateAndTime),
        remindAt = reminderTimeConversion.toZonedEpochMilli(
            startLocalDateTime = startDateAndTime,
            reminderTime = reminderTime,
            dateTimeConversion = dateTimeConversion
        ),
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskEntity(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): TaskEntity {
    return TaskEntity(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        time = dateTimeConversion.localDateTimeToZonedEpochMilli(startDateAndTime),
        remindAt = reminderTimeConversion.toZonedEpochMilli(
            startLocalDateTime = startDateAndTime,
            reminderTime = reminderTime,
            dateTimeConversion = dateTimeConversion
        )
    )
}

fun TaskEntity.toTask(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        isDone = isDone,
        title = title,
        description = description,
        startDateAndTime = dateTimeConversion.zonedEpochMilliToLocalDateTime(time),
        reminderTime = reminderTimeConversion.toEnum(
            startTimeEpochMilli = time,
            remindAtEpochMilli = remindAt,
            dateTimeConversion = dateTimeConversion
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
