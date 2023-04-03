package com.andrew.tasky.agenda.presentation.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.andrew.tasky.R
import com.andrew.tasky.core.MainActivity

class AgendaNotificationService(
    private val context: Context
) {

    private val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showEventNotification(title: String, description: String) {
        val eventNavIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.my_nav)
            .setDestination(R.id.reminderDetailFragment)
            .createPendingIntent()

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(EVENT_NAV_INTENT, eventNavIntent)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, EVENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_agenda)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)

        notificationManager.notify(1, notification.build())
    }

    companion object {
        const val AGENDA_GROUP_ID = "agenda_group"
        const val EVENT_CHANNEL_ID = "event_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val TASK_CHANNEL_ID = "task_channel"

        const val EVENT_NAV_INTENT = "event_nav_intent"
    }
}
