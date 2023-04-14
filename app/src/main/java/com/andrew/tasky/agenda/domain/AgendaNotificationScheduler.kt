package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaNotificationInfo

interface AgendaNotificationScheduler {
    fun schedule(agendaNotificationInfo: AgendaNotificationInfo)
    fun cancel(agendaNotificationInfo: AgendaNotificationInfo)
}
