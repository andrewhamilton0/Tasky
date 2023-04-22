package com.andrew.tasky.agenda.data.agenda.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andrew.tasky.agenda.data.agenda.notifications.AgendaNotificationSchedulerImpl.Companion.AGENDA_NOTIF_SCHED_INTENT
import com.andrew.tasky.agenda.data.agenda.toNotificationInfo
import com.andrew.tasky.agenda.domain.AgendaRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.*

@AndroidEntryPoint
class AgendaBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var agendaRepository: AgendaRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        val id = intent?.getStringExtra(AGENDA_NOTIF_SCHED_INTENT)
        val service = context?.let { AgendaNotificationService(it) }

        coroutineScope.launch {
            val item = id?.let { agendaRepository.getAgendaItemById(it) }
            val notificationInfo = item?.toNotificationInfo()
            notificationInfo?.let { service?.showNotification(it) }
        }
    }
}
