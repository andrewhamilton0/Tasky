package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationService.Companion.REMINDER_CHANNEL_ID
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo
import com.andrew.tasky.agenda.domain.toLocalDateTime
import com.andrew.tasky.agenda.domain.toZonedEpochMilli

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
        time = startDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
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
        time = startDateAndTime.toZonedEpochMilli(),
        remindAt = ReminderTimeConversion.toZonedEpochMilli(
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
        startDateAndTime = time.toLocalDateTime(),
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

fun AgendaItem.Reminder.toNotificationInfo(): AgendaNotificationInfo {
    return AgendaNotificationInfo(
        title = title,
        description = description,
        id = id,
        notificationChannel = REMINDER_CHANNEL_ID,
        navDestinationId = R.id.reminderDetailFragment,
        notificationZonedMilliTime = ReminderTimeConversion.toZonedEpochMilli(
            startDateAndTime,
            reminderTime
        )
    )
}
