package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationService
import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

fun AgendaItem.toNotificationInfo(
    dateTimeConversion: DateTimeConversion,
    reminderTimeConversion: ReminderTimeConversion
): AgendaNotificationInfo {

    val (channel, navId) = when (this) {
        is AgendaItem.Event -> AgendaNotificationService.EVENT_CHANNEL_ID to R.id.event_nav
        is AgendaItem.Reminder -> {
            AgendaNotificationService.REMINDER_CHANNEL_ID to R.id.reminderDetailFragment
        }
        is AgendaItem.Task -> AgendaNotificationService.TASK_CHANNEL_ID to R.id.taskDetailFragment
    }
    return AgendaNotificationInfo(
        title = title,
        description = description,
        id = id,
        notificationChannel = channel,
        navDestinationId = navId,
        notificationZonedMilliTime = reminderTimeConversion.toZonedEpochMilli(
            startLocalDateTime = startDateAndTime,
            reminderTime = reminderTime,
            dateTimeConversion = dateTimeConversion
        )
    )
}
