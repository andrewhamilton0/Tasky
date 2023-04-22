package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationService
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo
import com.andrew.tasky.agenda.domain.toLocalDateTime
import com.andrew.tasky.agenda.domain.toZonedEpochMilli

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
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
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
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
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

fun AgendaItem.Task.toNotificationInfo(): AgendaNotificationInfo {
    return AgendaNotificationInfo(
        title = title,
        description = description,
        id = id,
        notificationChannel = AgendaNotificationService.TASK_CHANNEL_ID,
        navDestinationId = R.id.taskDetailFragment,
        notificationZonedMilliTime = ReminderTimeConversion.toZonedEpochMilli(
            startDateAndTime,
            reminderTime
        )
    )
}
