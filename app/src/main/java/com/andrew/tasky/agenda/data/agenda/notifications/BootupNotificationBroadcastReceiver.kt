package com.andrew.tasky.agenda.data.agenda.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.andrew.tasky.agenda.domain.AgendaRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootupNotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var agendaRepository: AgendaRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            coroutineScope.launch {
                agendaRepository.sendPersistedNotifications()
            }
        }
    }
}
