package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.agenda.data.networkmodels.ReminderDto
import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
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

fun AgendaItem.Reminder.toReminderDto(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): ReminderDto {
    return ReminderDto(
        id = id,
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

fun AgendaItem.Reminder.toReminderEntity(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): ReminderEntity {
    return ReminderEntity(
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

fun ReminderEntity.toReminder(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): AgendaItem.Reminder {
    return AgendaItem.Reminder(
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

fun ReminderEntity.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt
    )
}
