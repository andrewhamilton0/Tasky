package com.andrew.tasky.agenda.data.agenda.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationSchedulerImpl.Companion.AGENDA_NOTIF_SCHED_INTENT
import com.andrew.tasky.agenda.data.event.toNotificationInfo
import com.andrew.tasky.agenda.data.reminder.toNotificationInfo
import com.andrew.tasky.agenda.data.task.toNotificationInfo
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*

@AndroidEntryPoint
class AgendaBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var agendaRepository: AgendaRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val id = intent?.getStringExtra(AGENDA_NOTIF_SCHED_INTENT)
        val service = context?.let { AgendaNotificationService(it) }

        coroutineScope.launch {
            val item = id?.let { agendaRepository.getAgendaItemById(it) }
            when (item) {
                is AgendaItem.Event -> service?.showNotification(item.toNotificationInfo())
                is AgendaItem.Reminder -> service?.showNotification(item.toNotificationInfo())
                is AgendaItem.Task -> service?.showNotification(item.toNotificationInfo())
                null -> Log.e("AgendaBroadcastReceiver", "null agenda item")
            }
        }
    }
}
