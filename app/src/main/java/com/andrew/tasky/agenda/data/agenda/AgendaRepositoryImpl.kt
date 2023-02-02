package com.andrew.tasky.agenda.data.agenda

import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.reminder.toReminder
import com.andrew.tasky.agenda.data.reminder.toReminderEntity
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AgendaRepositoryImpl(
    private val agendaApi: AgendaApi,
    private val reminderRepository: ReminderRepository,
    private val db: AgendaDatabase,
) : AgendaRepository {

    override suspend fun getAgendaItems(localDate: LocalDate): Flow<List<AgendaItem>> {

        val startEpochMilli = localDateTimeToZonedEpochMilli(localDate.atStartOfDay())
        val endEpochMilli = localDateTimeToZonedEpochMilli(localDate.atStartOfDay().plusDays(1))

        syncAgendaItems()
        when (
            val results = getAuthResult {
                agendaApi.getAgendaItems(
                    timezone = TimeZone.getDefault().id,
                    time = localDateTimeToZonedEpochMilli(
                        LocalDateTime.of(localDate, LocalTime.now())
                    )
                )
            }
        ) {
            is AuthResult.Authorized -> {
                val localReminders = db.getReminderDao().getRemindersOfDate(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                )
                localReminders.forEach { localReminder ->
                    val containsLocalId = results.data?.reminders?.any {
                        it.id == localReminder.id
                    } == true
                    if (!containsLocalId) {
                        db.getReminderDao().deleteReminder(localReminder)
                    }
                }
                results.data?.reminders?.forEach { reminderDto ->
                    val localReminder = db.getReminderDao().getReminderById(reminderDto.id)
                    db.getReminderDao().upsertReminder(
                        reminderDto.toReminderEntity(
                            isDone = localReminder?.isDone ?: false
                        )
                    )
                }
            }
            is AuthResult.Unauthorized -> {
                Log.e("Update Agenda of Date", "Unauthorized")
            }
            is AuthResult.UnknownError -> {
                Log.e("Update Agenda of Date", "Unknown Error")
            }
        }

        return db.getReminderDao().getRemindersOfDateFlow(startEpochMilli, endEpochMilli).map {
            it.map { reminderEntity ->
                reminderEntity.toReminder()
            }.sortedBy { reminder ->
                reminder.startDateAndTime
            }
        }
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
}
