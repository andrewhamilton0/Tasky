package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.event.EventDatabase
import com.andrew.tasky.agenda.data.reminder.ReminderDatabase
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult

class AgendaRepositoryImpl(
    private val api: AgendaApi,
    private val reminderDb: ReminderDatabase,
    private val eventDb: EventDatabase
) : AgendaRepository {

    override suspend fun getRemoteAgendaItems(
        timezone: String,
        time: Long
    ): AuthResult<Unit> {
        return getAuthResult {
            val response = api.getAgendaItems(timezone = timezone, time = time)
            val tasks = response.taskDtos.map { task ->
                task.toTask()
            }
            response.reminderEntities.map { reminder ->
                reminderDb.getReminderDao().upsert(reminder)
            }
            // TODO Make event and task db connect to api
        }
    }

    override suspend fun syncAgendaItems(deleteIds: SyncAgendaRequest): AuthResult<Unit> {
        return getAuthResult { api.syncAgendaItems(deleteIds) }
    }
}
