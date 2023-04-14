package com.andrew.tasky.agenda.data.agenda.notifications

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import com.andrew.tasky.R

class AgendaNotificationChannels(
    val context: Context
) {

    fun createAgendaNotificationChannels() {
        createAgendaGroup()
        createEventNotificationChannel()
        createTaskNotificationChannel()
        createReminderNotificationChannel()
    }

    private fun createAgendaGroup() {
        val groupId = AgendaNotificationService.AGENDA_GROUP_ID
        val groupName = context.getString(R.string.agenda)
        val notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.createNotificationChannelGroup(
            NotificationChannelGroup(groupId, groupName)
        )
    }

    private fun createEventNotificationChannel() {
        val channel = NotificationChannel(
            AgendaNotificationService.EVENT_CHANNEL_ID,
            context.getString(R.string.event),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(
            R.string.used_for_string_notification, context.getString(R.string.event).lowercase()
        )
        channel.group = AgendaNotificationService.AGENDA_GROUP_ID

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createReminderNotificationChannel() {
        val channel = NotificationChannel(
            AgendaNotificationService.REMINDER_CHANNEL_ID,
            context.getString(R.string.reminder),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(
            R.string.used_for_string_notification, context.getString(R.string.reminder).lowercase()
        )
        channel.group = AgendaNotificationService.AGENDA_GROUP_ID

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createTaskNotificationChannel() {
        val channel = NotificationChannel(
            AgendaNotificationService.TASK_CHANNEL_ID,
            context.getString(R.string.task),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(
            R.string.used_for_string_notification, context.getString(R.string.task).lowercase()
        )
        channel.group = AgendaNotificationService.AGENDA_GROUP_ID

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
