package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.agenda.data.util.ReminderTimeConversion
import com.andrew.tasky.agenda.data.util.localDateTimeToZonedEpochMilli
import com.andrew.tasky.agenda.data.util.zonedEpochMilliToLocalDateTime
import com.andrew.tasky.agenda.domain.models.AgendaItem

fun ReminderDto.toReminderEntity(isDone: Boolean): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description ?: "",
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun AgendaItem.Reminder.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = localDateTimeToZonedEpochMilli(startDateAndTime),
        remindAt = ReminderTimeConversion.toEpochMilli(
            startLocalDateTime = startDateAndTime,
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
        time = localDateTimeToZonedEpochMilli(startDateAndTime),
        remindAt = ReminderTimeConversion.toEpochMilli(
            startLocalDateTime = startDateAndTime,
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
        startDateAndTime = zonedEpochMilliToLocalDateTime(time),
        reminderTime = ReminderTimeConversion.toEnum(
            startTimeEpochMilli = time,
            remindAtEpochMilli = remindAt
        )
    )
}

fun ReminderEntity.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt
    )
}
