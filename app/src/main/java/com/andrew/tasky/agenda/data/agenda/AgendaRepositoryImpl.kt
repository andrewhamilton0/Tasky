package com.andrew.tasky.agenda.data.agenda

import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.reminder.ReminderApi
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgendaRepositoryImpl(
    private val agendaApi: AgendaApi,
    private val reminderRepository: ReminderRepository,
    private val db: AgendaDatabase,
) : AgendaRepository {

    override fun getAgendaItems(): Flow<List<AgendaItem>> {
        val localReminders = db.getReminderDao().getRemindersAsFlow().map {
            it.map { reminderEntity ->
                reminderEntity.toReminder()
            }
        }
        return localReminders
        // Todo return task and event agenda items
    }

    override suspend fun syncAgendaItems() {
        reminderRepository.uploadCreateAndUpdateModifiedReminders()

        val reminderDeleteIds = db.getReminderDao().getModifiedReminders().filter {
            it.modifiedType == ModifiedType.DELETE
        }.map {
            it.id
        }
        val syncAgendaRequest = SyncAgendaRequest(emptyList(), emptyList(), reminderDeleteIds)
        when (getAuthResult { agendaApi.syncAgendaItems(syncAgendaRequest) }) {
            is AuthResult.Authorized -> {
                reminderDeleteIds.map {
                    db.getReminderDao().deleteModifiedReminderById(it)
                }
            }
            is AuthResult.Unauthorized -> Log.e(
                "SyncAgendaItem",
                "Unauthorized, could not run agendaApi.syncAgendaItems(syncAgendaRequest)"
            )
            is AuthResult.UnknownError -> Log.e(
                "SyncAgendaItem",
                "Unknown Error, could not run agendaApi.syncAgendaItems(syncAgendaRequest)"
            )
        }
    }

    override suspend fun updateAgendaOfDate(date: LocalDate) {
        syncAgendaItems()
        when (
            val results = getAuthResult {
                agendaApi.getAgendaItems(
                    timezone = TimeZone.getDefault().id,
                    time = date.atTime(LocalTime.now())
                        .atZone(TimeZone.getDefault().toZoneId()).toEpochSecond()
                )
            }
        ) {
            is AuthResult.Authorized -> {
                val localReminders = db.getReminderDao().getOneTimeListOfReminders()
                localReminders.map { localReminder ->
                    val remoteReminderIds = results.data?.reminders?.map { it.id } ?: emptyList()
                    if (!remoteReminderIds.contains(localReminder.id)) {
                        db.getReminderDao().deleteReminder(localReminder)
                    }
                }
                results.data?.reminders?.map { reminderDto ->
                    val localReminder = db.getReminderDao().getReminderById(reminderDto.id)
                    db.getReminderDao().upsertReminder(
                        reminderDto.toReminderEntity(
                            // Android Studio says local reminder will never equal null,
                            // but can if reminder doesn't exist in db
                            isDone = localReminder.isDone ?: false
                        )
                    )
                }
            }
            is AuthResult.Unauthorized -> Log.e("Update Agenda of Date", "Unauthorized")
            is AuthResult.UnknownError -> Log.e("Update Agenda of Date", "Unknown Error")
        }
    }
}
