package com.andrew.tasky.agenda.data.agenda.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

class AgendaNotificationSchedulerImpl(
    private val context: Context
) : AgendaNotificationScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(agendaNotificationInfo: AgendaNotificationInfo) {

        val intent = Intent(context, AgendaBroadcastReceiver::class.java)
            .putExtra(AGENDA_NOTIF_SCHED_INTENT, agendaNotificationInfo)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            agendaNotificationInfo.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            agendaNotificationInfo.notificationZonedMilliTime,
            pendingIntent
        )
    }

    override fun cancel(agendaNotificationInfo: AgendaNotificationInfo) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                agendaNotificationInfo.id.hashCode(),
                Intent(context, AgendaNotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        const val AGENDA_NOTIF_SCHED_INTENT = "AGENDA_NOTIF_SCHED_INTENT"
    }
}
