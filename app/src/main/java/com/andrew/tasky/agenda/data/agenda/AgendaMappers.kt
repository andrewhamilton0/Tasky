package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationService
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

fun AgendaItem.toNotificationInfo(): AgendaNotificationInfo {
    return when (this) {
        is AgendaItem.Event -> {
            AgendaNotificationInfo(
                title = title,
                description = description,
                id = id,
                notificationChannel = AgendaNotificationService.EVENT_CHANNEL_ID,
                navDestinationId = R.id.event_nav,
                notificationZonedMilliTime = ReminderTimeConversion.toZonedEpochMilli(
                    startDateAndTime,
                    reminderTime
                )
            )
        }
        is AgendaItem.Reminder -> {
            AgendaNotificationInfo(
                title = title,
                description = description,
                id = id,
                notificationChannel = AgendaNotificationService.REMINDER_CHANNEL_ID,
                navDestinationId = R.id.reminderDetailFragment,
                notificationZonedMilliTime = ReminderTimeConversion.toZonedEpochMilli(
                    startDateAndTime,
                    reminderTime
                )
            )
        }
        is AgendaItem.Task -> {
            AgendaNotificationInfo(
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
    }
}
