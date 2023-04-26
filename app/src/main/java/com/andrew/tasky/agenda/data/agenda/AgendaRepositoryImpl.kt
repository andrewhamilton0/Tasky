package com.andrew.tasky.agenda.data.agenda

import android.app.AlarmManager
import android.content.Context
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
import com.andrew.tasky.agenda.domain.*
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.data.Resource
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AgendaRepositoryImpl(
    private val agendaApi: AgendaApi,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val db: AgendaDatabase,
    private val appContext: Context,
    private val scheduler: AgendaNotificationScheduler
) : AgendaRepository {

    private val alarmManager = appContext.getSystemService(AlarmManager::class.java)

    override suspend fun getAgendaItemsOfDateFlow(localDate: LocalDate): Flow<List<AgendaItem>> {
        val startEpochMilli = localDate.atStartOfDay().toZonedEpochMilli()
        val endEpochMilli = localDate.atStartOfDay().plusDays(1).toZonedEpochMilli()

        val reminders = db.getReminderDao().getRemindersBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { reminderEntity ->
                reminderEntity.toReminder()
            }
        }
        val tasks = db.getTaskDao().getTasksBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { taskEntity ->
                taskEntity.toTask()
            }
        }
        val events = db.getEventDao().getEventsBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            supervisorScope {
                it.map { eventEntity ->
                    async {
                        eventEntity.toEvent(eventRepository)
                    }
                }.map { it.await() }
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

    override suspend fun getAgendaItemById(id: String): AgendaItem? {
        val event = eventRepository.getEvent(id)
        val task = taskRepository.getTask(id)
        val reminder = reminderRepository.getReminder(id)

        val agendaItems = listOf(event, task, reminder)
        return agendaItems.find { it != null }
    }

    override suspend fun updateAgendaItemCache(localDate: LocalDate) {
        val startEpochMilli = localDate.atStartOfDay().toZonedEpochMilli()
        val endEpochMilli = localDate.atStartOfDay().plusDays(1).toZonedEpochMilli()

        syncModifiedAgendaItems()
        val results = getResourceResult {
            agendaApi.getAgendaItems(
                timezone = TimeZone.getDefault().id,
                time = LocalDateTime.of(localDate, LocalTime.now()).toZonedEpochMilli()
            )
        }
        when (results) {
            is Resource.Error -> {
                Log.e(
                    "Update Agenda of Date Error",
                    results.message?.asString(appContext) ?: "Unknown Error"
                )
            }
            is Resource.Success -> {
                val localReminders = db.getReminderDao().getRemindersBetweenTimes(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                ).first()
                localReminders.forEach { localReminder ->
                    val containsLocalId = results.data?.reminders?.any {
                        it.id == localReminder.id
                    } == true
                    if (!containsLocalId) {
                        db.getReminderDao().deleteReminder(localReminder.id)
                        cancelScheduledNotification(localReminder.id)
                    }
                }
                results.data?.reminders?.forEach { reminderDto ->
                    val localReminder = db.getReminderDao().getReminderById(reminderDto.id)
                    val remoteReminder = reminderDto.toReminderEntity(
                        isDone = localReminder?.isDone ?: false
                    )
                    db.getReminderDao().upsertReminder(remoteReminder)
                    scheduleNotification(remoteReminder.toReminder())
                }
                val localTasks = db.getTaskDao().getTasksBetweenTimes(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                ).first()
                localTasks.forEach { localTask ->
                    val containsLocalId = results.data?.tasks?.any {
                        it.id == localTask.id
                    } == true
                    if (!containsLocalId) {
                        db.getTaskDao().deleteTask(localTask.id)
                        cancelScheduledNotification(localTask.id)
                    }
                }
                results.data?.tasks?.forEach { taskDto ->
                    db.getTaskDao().upsertTask(taskDto.toTaskEntity())
                    scheduleNotification(taskDto.toTaskEntity().toTask())
                }
                val localEvents = db.getEventDao().getEventsBetweenTimes(
                    startEpochMilli = startEpochMilli,
                    endEpochMilli = endEpochMilli
                ).first()
                localEvents.forEach { localEvent ->
                    val containsLocalId = results.data?.events?.any {
                        it.id == localEvent.id
                    } == true
                    if (!containsLocalId) {
                        db.getEventDao().deleteEvent(localEvent.id)
                        cancelScheduledNotification(localEvent.id)
                    }
                }
                results.data?.events?.forEach { eventDto ->
                    val localEvent = db.getEventDao().getEventById(eventDto.id)
                    val remoteEvent = eventDto.toEventEntity(
                        isDone = localEvent?.isDone ?: false,
                        isGoing = localEvent?.isGoing ?: true
                    )
                    db.getEventDao().upsertEvent(remoteEvent)
                    scheduleNotification(remoteEvent.toEvent(eventRepository))
                }
            }
        }
    }

    override suspend fun syncModifiedAgendaItems(): Resource<Unit> {
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
            val results = getResourceResult { agendaApi.syncAgendaItems(syncAgendaRequest) }
            when (results) {
                is Resource.Error -> {
                    Log.e(
                        "SyncAgendaItem Error",
                        results.message?.asString(appContext) ?: "Unknown Error"
                    )
                }
                is Resource.Success -> {
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
            }
            return results
        }
        return Resource.Success()
    }

    override suspend fun syncFullAgenda() {
        syncModifiedAgendaItems()
        val results = getResourceResult { agendaApi.getFullAgenda() }
        when (results) {
            is Resource.Error -> {
                Log.e(
                    "Update Agenda of Date Error",
                    results.message?.asString(appContext) ?: "Unknown Error"
                )
            }
            is Resource.Success -> {
                val localReminders = db.getReminderDao().getAllReminders().first()
                localReminders.forEach { localReminder ->
                    val containsLocalId = results.data?.reminders?.any {
                        it.id == localReminder.id
                    } == true
                    if (!containsLocalId) {
                        db.getReminderDao().deleteReminder(localReminder.id)
                        cancelScheduledNotification(localReminder.id)
                    }
                }
                results.data?.reminders?.forEach { reminderDto ->
                    val localReminder = db.getReminderDao().getReminderById(reminderDto.id)
                    val remoteReminder = reminderDto.toReminderEntity(
                        isDone = localReminder?.isDone ?: false
                    )
                    db.getReminderDao().upsertReminder(remoteReminder)
                    scheduleNotification(remoteReminder.toReminder())
                }

                val localTasks = db.getTaskDao().getAllTasks().first()
                localTasks.forEach { localTask ->
                    val containsLocalId = results.data?.tasks?.any {
                        it.id == localTask.id
                    } == true
                    if (!containsLocalId) {
                        db.getTaskDao().deleteTask(localTask.id)
                        cancelScheduledNotification(localTask.id)
                    }
                }
                results.data?.tasks?.forEach { taskDto ->
                    db.getTaskDao().upsertTask(taskDto.toTaskEntity())
                    scheduleNotification(taskDto.toTaskEntity().toTask())
                }
                val localEvents = db.getEventDao().getAllEvents().first()
                localEvents.forEach { localEvent ->
                    val containsLocalId = results.data?.events?.any {
                        it.id == localEvent.id
                    } == true
                    if (!containsLocalId) {
                        db.getEventDao().deleteEvent(localEvent.id)
                        cancelScheduledNotification(localEvent.id)
                    }
                }
                results.data?.events?.forEach { eventDto ->
                    val localEvent = db.getEventDao().getEventById(eventDto.id)
                    val remoteEvent = eventDto.toEventEntity(
                        isDone = localEvent?.isDone ?: false,
                        isGoing = localEvent?.isGoing ?: true
                    )
                    db.getEventDao().upsertEvent(remoteEvent)
                    scheduleNotification(remoteEvent.toEvent(eventRepository))
                }
            }
        }
    }

    override suspend fun deleteAllAgendaItems() {
        deleteAllAgendaTables()
        cancelAllNotifications()
    }

    private suspend fun deleteAllAgendaTables() {
        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private suspend fun cancelAllNotifications() {
        withContext(Dispatchers.Main) {
            var alarm = alarmManager.nextAlarmClock
            while (alarm != null) {
                alarm.showIntent.cancel()
                alarm = alarmManager.nextAlarmClock
            }
        }
    }

    private fun scheduleNotification(agendaItem: AgendaItem) {
        scheduler.schedule(
            agendaId = agendaItem.id,
            time = ReminderTimeConversion.toZonedEpochMilli(
                startLocalDateTime = agendaItem.startDateAndTime,
                reminderTime = agendaItem.reminderTime
            )
        )
    }

    private fun cancelScheduledNotification(agendaItemId: String) {
        scheduler.cancel(agendaItemId)
    }
}
