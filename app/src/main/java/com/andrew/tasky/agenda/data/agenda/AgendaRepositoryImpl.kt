package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult

class AgendaRepositoryImpl(
    private val api: AgendaApi
) : AgendaRepository {

    override suspend fun getAgendaItems(
        timezone: String,
        time: Long
    ): AuthResult<List<AgendaItem>> {
        return getAuthResult {
            val response = api.getAgendaItems(timezone = timezone, time = time)
            val tasks = response.taskDtos.map { task ->
                task.toTask()
            }
            val reminders = response.reminderDtos.map { reminder ->
                reminder.toReminder()
            }
            val events = response.eventDtos.map { event ->
                event.toEvent()
            }
            tasks + events + reminders
        }
    }

    override suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse> {
        return getAuthResult { api.syncAgendaItems() }
    }
}
