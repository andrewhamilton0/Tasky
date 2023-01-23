package com.andrew.tasky.agenda.data.agenda

import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgendaRepositoryImpl(
    private val api: AgendaApi,
    private val db: AgendaDatabase,
) : AgendaRepository {

    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        val localReminders = db.getReminderDao().getReminders().map {
            it.map { reminderEntity ->
                reminderEntity.toReminder()
            }
        }
        return localReminders
        // Todo return task and event agenda items
    }

    override suspend fun syncAgendaItems(): AuthResult<Unit> {
        val reminderDeleteIds = db.getReminderDao().getModifiedReminders().filter {
            it.modifiedType == ModifiedType.DELETE
        }.map {
            it.id
        }
        val syncAgendaRequest = SyncAgendaRequest(emptyList(), emptyList(), reminderDeleteIds)
        return getAuthResult { api.syncAgendaItems(syncAgendaRequest) }
    }

    override suspend fun updateAgendaOfDate(time: Long, timezone: String) {
        when (
            val results = getAuthResult {
                api.getAgendaItems(
                    timezone,
                    time
                )
            }
        ) {
            is AuthResult.Authorized -> {
                val remoteReminders = results.data?.reminderDtos?.map { reminderDto ->
                    reminderDto.toReminder()
                }
                remoteReminders?.map { reminder ->
                    db.getReminderDao().upsertReminder(reminder.toReminderEntity())
                }
            }
            is AuthResult.Unauthorized -> Log.e("Update Agenda of Date", "Unauthorized")
            is AuthResult.UnknownError -> Log.e("Update Agenda of Date", "Unknown Error")
        }
    }
}
