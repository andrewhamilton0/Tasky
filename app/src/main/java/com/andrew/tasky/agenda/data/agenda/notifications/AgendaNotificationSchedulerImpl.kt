package com.andrew.tasky.agenda.data.agenda.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler

class AgendaNotificationSchedulerImpl(
    private val context: Context
) : AgendaNotificationScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(agendaId: String, time: Long) {

        val intent = Intent(context, AgendaBroadcastReceiver::class.java)
            .putExtra(AGENDA_NOTIF_SCHED_INTENT, agendaId)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            agendaId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    override fun cancel(agendaId: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                agendaId.hashCode(),
                Intent(context, AgendaNotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        const val AGENDA_NOTIF_SCHED_INTENT = "AGENDA_NOTIF_SCHED_INTENT"
    }
}
