package com.andrew.tasky.agenda.data.agenda.notifications

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.andrew.tasky.R
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

class AgendaNotificationService(
    private val context: Context
) {

    private val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(info: AgendaNotificationInfo) {
        val navArs = Bundle().apply {
            putString("id", info.id)
        }

        val navIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.my_nav)
            .setDestination(info.navDestinationId)
            .setArguments(navArs)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, info.notificationChannel)
            .setSmallIcon(R.drawable.ic_notification_agenda)
            .setContentTitle(info.title)
            .setContentText(info.description)
            .setContentIntent(navIntent)

        notificationManager.notify(info.id.hashCode(), notification.build())
    }

    companion object {
        const val AGENDA_GROUP_ID = "agenda_group"
        const val EVENT_CHANNEL_ID = "event_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val TASK_CHANNEL_ID = "task_channel"
    }
}
