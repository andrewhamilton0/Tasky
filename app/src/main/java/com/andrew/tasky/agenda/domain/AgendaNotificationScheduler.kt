package com.andrew.tasky.agenda.domain

interface AgendaNotificationScheduler {
    fun schedule(agendaId: String, time: Long)
    fun cancel(agendaId: String)
}
