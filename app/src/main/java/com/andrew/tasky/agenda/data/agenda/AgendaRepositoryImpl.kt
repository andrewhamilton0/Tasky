package com.andrew.tasky.agenda.data.agenda

import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.reminder.toReminder
import com.andrew.tasky.agenda.data.reminder.toReminderEntity
import com.andrew.tasky.agenda.data.task.toTask
import com.andrew.tasky.agenda.data.task.toTaskEntity
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import kotlinx.coroutines.flow.*

class AgendaRepositoryImpl(
    private val agendaApi: AgendaApi,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val db: AgendaDatabase,
) : AgendaRepository {

    override suspend fun getAgendaItems(localDate: LocalDate) = flow<List<AgendaItem>> {
        val startEpochMilli = localDateTimeToZonedEpochMilli(localDate.atStartOfDay())
        val endEpochMilli = localDateTimeToZonedEpochMilli(localDate.atStartOfDay().plusDays(1))

        val reminders = db.getReminderDao().getRemindersOfDate(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { reminderEntity ->
                reminderEntity.toReminder()
            }
        }
        val tasks = db.getTaskDao().getTasksOfDate(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { taskEntity ->
                taskEntity.toTask()
            }
        }

        emit(
            reminders.combine(tasks) { reminderFlow, taskFlow ->
                reminderFlow + taskFlow
            }.map {
                it.sortedBy { agendaItem ->
                    agendaItem.startDateAndTime
                }
            }.first()
        )

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
                ).first()
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
                val localTasks = db.getTaskDao().getTasksOfDate(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                ).first()
                localTasks.forEach { localTask ->
                    val containsLocalId = results.data?.tasks?.any {
                        it.id == localTask.id
                    } == true
                    if (!containsLocalId) {
                        db.getTaskDao().deleteTask(localTask)
                    }
                }
                results.data?.tasks?.forEach { taskDto ->
                    db.getTaskDao().upsertTask(taskDto.toTaskEntity())
                }
            }
            is AuthResult.Unauthorized -> {
                Log.e("Update Agenda of Date", "Unauthorized")
            }
            is AuthResult.UnknownError -> {
                Log.e("Update Agenda of Date", "Unknown Error")
            }
        }
        emitAll(
            reminders.combine(tasks) { reminderFlow, taskFlow ->
                reminderFlow + taskFlow
            }.map {
                it.sortedBy { agendaItem ->
                    agendaItem.startDateAndTime
                }
            }
        )
    }

    override suspend fun syncAgendaItems() {
        reminderRepository.uploadCreateAndUpdateModifiedReminders()
        taskRepository.uploadCreateAndUpdateModifiedTasks()

        val reminderDeleteIds = db.getReminderDao().getModifiedReminders().filter {
            it.modifiedType == ModifiedType.DELETE
        }.map {
            it.id
        }
        val taskDeleteIds = db.getTaskDao().getModifiedTasks().filter {
            it.modifiedType == ModifiedType.DELETE
        }.map {
            it.id
        }

        if (reminderDeleteIds.isNotEmpty() || taskDeleteIds.isNotEmpty()) {
            val syncAgendaRequest = SyncAgendaRequest(emptyList(), taskDeleteIds, reminderDeleteIds)
            val result = getAuthResult { agendaApi.syncAgendaItems(syncAgendaRequest) }
            when (result) {
                is AuthResult.Authorized -> {
                    reminderDeleteIds.forEach {
                        db.getReminderDao().deleteModifiedReminderById(it)
                    }
                    taskDeleteIds.forEach {
                        db.getTaskDao().deleteModifiedTaskById(it)
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
            // Todo sync events
        }
    }

    override suspend fun deleteAllAgendaTables() {
        db.clearAllTables()
    }
}
