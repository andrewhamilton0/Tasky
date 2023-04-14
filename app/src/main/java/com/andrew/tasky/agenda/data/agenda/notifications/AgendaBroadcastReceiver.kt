package com.andrew.tasky.agenda.data.agenda.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationSchedulerImpl.Companion.AGENDA_NOTIF_SCHED_INTENT
import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

class AgendaBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val info = intent?.getParcelableExtra(
            AGENDA_NOTIF_SCHED_INTENT,
            AgendaNotificationInfo::class.java
        )
        val service = context?.let { AgendaNotificationService(it) }
        info?.let { service?.showNotification(it) }
    }
}
