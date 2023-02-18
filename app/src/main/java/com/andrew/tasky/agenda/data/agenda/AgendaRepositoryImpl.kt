package com.andrew.tasky.agenda.data.agenda

import android.icu.util.TimeZone
import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.event.toEvent
import com.andrew.tasky.agenda.data.event.toEventEntity
import com.andrew.tasky.agenda.data.reminder.toReminder
import com.andrew.tasky.agenda.data.reminder.toReminderEntity
import com.andrew.tasky.agenda.data.task.toTask
import com.andrew.tasky.agenda.data.task.toTaskEntity
import com.andrew.tasky.agenda.data.util.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class AgendaRepositoryImpl(
    private val agendaApi: AgendaApi,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val db: AgendaDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : AgendaRepository {

    override suspend fun getAgendaItems(localDate: LocalDate): Flow<List<AgendaItem>> {
        val startEpochMilli = localDate.atStartOfDay().toZonedEpochMilli()
        val endEpochMilli = localDate.atStartOfDay().plusDays(1).toZonedEpochMilli()

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
        val events = db.getEventDao().getEventsOfDate(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map {
                eventEntity ->
                eventEntity.toEvent()
            }
        }

        return combine(reminders, tasks, events) { _reminders, _tasks, _events ->
            _reminders + _tasks + _events
        }.map {
            it.sortedBy { agendaItem ->
                agendaItem.startDateAndTime
            }
        }
    }

    override suspend fun updateAgendaItemCache(localDate: LocalDate) {
        val startEpochMilli = localDate.atStartOfDay().toZonedEpochMilli()
        val endEpochMilli = localDate.atStartOfDay().plusDays(1).toZonedEpochMilli()

        syncModifiedAgendaItems()
        val results = getAuthResult {
            agendaApi.getAgendaItems(
                timezone = TimeZone.getDefault().id,
                time = LocalDateTime.of(localDate, LocalTime.now()).toZonedEpochMilli()
            )
        }
        when (results) {
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
                    val remoteReminder = reminderDto.toReminderEntity(
                        isDone = localReminder?.isDone ?: false
                    )
                    db.getReminderDao().upsertReminder(remoteReminder)
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
                val localEvents = db.getEventDao().getEventsOfDate(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                ).first()
                localEvents.forEach { localEvent ->
                    val containsLocalId = results.data?.reminders?.any {
                        it.id == localEvent.id
                    } == true
                    if (!containsLocalId) {
                        db.getEventDao().deleteEvent(localEvent)
                    }
                }
                results.data?.events?.forEach { eventDto ->
                    val localEvent = db.getEventDao().getEventById(eventDto.id)
                    val remoteEvent = eventDto.toEventEntity(
                        isDone = localEvent?.isDone ?: false,
                        isGoing = localEvent?.isGoing ?: true
                    )
                    if (localEvent != remoteEvent) {
                        db.getEventDao().upsertEvent(remoteEvent)
                    }
                }
            }
            is AuthResult.Unauthorized -> {
                Log.e("Update Agenda of Date", "Unauthorized")
            }
            is AuthResult.UnknownError -> {
                Log.e("Update Agenda of Date", "Unknown Error")
            }
        }
    }

    override suspend fun syncModifiedAgendaItems() {
        reminderRepository.uploadCreateAndUpdateModifiedReminders()
        taskRepository.uploadCreateAndUpdateModifiedTasks()
        eventRepository.uploadCreateAndUpdateModifiedEvents()

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
        val eventDeleteIds = db.getEventDao().getModifiedEvents().filter {
            it.modifiedType == ModifiedType.DELETE
        }.map {
            it.id
        }

        if (reminderDeleteIds.isNotEmpty() ||
            taskDeleteIds.isNotEmpty() ||
            eventDeleteIds.isNotEmpty()
        ) {
            val syncAgendaRequest = SyncAgendaRequest(
                eventDeleteIds,
                taskDeleteIds,
                reminderDeleteIds
            )
            val result = getAuthResult { agendaApi.syncAgendaItems(syncAgendaRequest) }
            when (result) {
                is AuthResult.Authorized -> {
                    reminderDeleteIds.forEach {
                        db.getReminderDao().deleteModifiedReminderById(it)
                    }
                    taskDeleteIds.forEach {
                        db.getTaskDao().deleteModifiedTaskById(it)
                    }
                    eventDeleteIds.forEach {
                        db.getEventDao().deleteModifiedEventById(it)
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

    override suspend fun deleteAllAgendaTables() {
        withContext(ioDispatcher) {
            db.clearAllTables()
        }
    }
}
