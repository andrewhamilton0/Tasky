package com.andrew.tasky.agenda.data.agenda

import android.content.Context
import android.icu.util.TimeZone
import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.event.toEvent
import com.andrew.tasky.agenda.data.event.toEventEntity
import com.andrew.tasky.agenda.data.networkmodels.SyncAgendaRequest
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
    private val scheduler: AgendaNotificationScheduler,
    private val dateTimeConversion: DateTimeConversion,
    private val reminderTimeConversion: ReminderTimeConversion
) : AgendaRepository {

    override suspend fun getAgendaItemsOfDateFlow(localDate: LocalDate): Flow<List<AgendaItem>> {
        val startEpochMilli = dateTimeConversion.localDateTimeToZonedEpochMilli(
            localDate.atStartOfDay()
        )
        val endEpochMilli = dateTimeConversion.localDateTimeToZonedEpochMilli(
            localDate.atStartOfDay().plusDays(1)
        )

        val reminders = db.getReminderDao().getRemindersBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { reminderEntity ->
                reminderEntity.toReminder(
                    dateTimeConversion = dateTimeConversion,
                    reminderTimeConversion = reminderTimeConversion
                )
            }
        }
        val tasks = db.getTaskDao().getTasksBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            it.map { taskEntity ->
                taskEntity.toTask(
                    dateTimeConversion = dateTimeConversion,
                    reminderTimeConversion = reminderTimeConversion
                )
            }
        }
        val events = db.getEventDao().getEventsBetweenTimes(
            startEpochMilli = startEpochMilli,
            endEpochMilli = endEpochMilli
        ).map {
            supervisorScope {
                it.map { eventEntity ->
                    async {
                        eventEntity.toEvent(
                            eventRepository = eventRepository,
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
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

    // TODO DELETE ALL AGENDA ITEMS AND REUPSERT ALL LOCALLY
    override suspend fun updateAgendaItemCache(localDate: LocalDate) {
        val startEpochMilli = dateTimeConversion.localDateTimeToZonedEpochMilli(
            localDate.atStartOfDay()
        )
        val endEpochMilli = dateTimeConversion.localDateTimeToZonedEpochMilli(
            localDate.atStartOfDay().plusDays(1)
        )

        syncModifiedAgendaItems()
        val results = getResourceResult {
            agendaApi.getAgendaItems(
                timezone = TimeZone.getDefault().id,
                time = dateTimeConversion.localDateTimeToZonedEpochMilli(
                    LocalDateTime.of(localDate, LocalTime.now())
                )
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
                    scheduleNotification(
                        remoteReminder.toReminder(
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
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
                    scheduleNotification(
                        taskDto.toTaskEntity().toTask(
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
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
                    scheduleNotification(
                        remoteEvent.toEvent(
                            eventRepository = eventRepository,
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
                }
            }
        }
    }

    override suspend fun syncModifiedAgendaItems(): Resource<Unit> {
        reminderRepository.uploadCreateAndUpdateModifiedReminders()
        taskRepository.uploadCreateAndUpdateModifiedTasks()
        eventRepository.uploadCreateAndUpdateModifiedEvents()

        return supervisorScope {
            async {
                val reminderDeleteIdsAsync = async {
                    db.getReminderDao().getModifiedReminders().first().filter {
                        it.modifiedType == ModifiedType.DELETE
                    }.map {
                        it.id
                    }
                }
                val taskDeleteIdsAsync = async {
                    db.getTaskDao().getModifiedTasks().first().filter {
                        it.modifiedType == ModifiedType.DELETE
                    }.map {
                        it.id
                    }
                }
                val eventDeleteIdsAsync = async {
                    db.getEventDao().getModifiedEvents().first().filter {
                        it.modifiedType == ModifiedType.DELETE
                    }.map {
                        it.id
                    }
                }
                val eventDeleteIds = eventDeleteIdsAsync.await()
                val taskDeleteIds = taskDeleteIdsAsync.await()
                val reminderDeleteIds = reminderDeleteIdsAsync.await()

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
                            launch {
                                reminderDeleteIds.forEach {
                                    db.getReminderDao().deleteModifiedReminderById(it)
                                }
                            }
                            launch {
                                taskDeleteIds.forEach {
                                    db.getTaskDao().deleteModifiedTaskById(it)
                                }
                            }
                            launch {
                                eventDeleteIds.forEach {
                                    db.getEventDao().deleteModifiedEventById(it)
                                }
                            }
                        }
                    }
                    return@async results
                } else {
                    return@async Resource.Success()
                }
            }.await()
        }
    }

    // TODO DELETE ALL AGENDA ITEMS AND REUPSERT ALL LOCALLY
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
                    scheduleNotification(
                        remoteReminder.toReminder(
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
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
                    scheduleNotification(
                        taskDto.toTaskEntity().toTask(
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
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
                    scheduleNotification(
                        remoteEvent.toEvent(
                            eventRepository = eventRepository,
                            dateTimeConversion = dateTimeConversion,
                            reminderTimeConversion = reminderTimeConversion
                        )
                    )
                }
            }
        }
    }

    override suspend fun deleteAllAgendaItems() {
        cancelAllNotifications()
        deleteAllAgendaTables()
    }

    override suspend fun scheduleAllAgendaItemNotifications() {
        val agendaItems = getAllAgendaItems().first()
        agendaItems.forEach { item ->
            scheduleNotification(item)
        }
    }

    private suspend fun deleteAllAgendaTables() {
        withContext(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private suspend fun cancelAllNotifications() {
        withContext(Dispatchers.Main) {
            getAllAgendaItems().first().forEach {
                supervisorScope {
                    cancelScheduledNotification(it.id)
                }
            }
        }
    }

    private fun scheduleNotification(agendaItem: AgendaItem) {
        scheduler.schedule(
            agendaId = agendaItem.id,
            time = reminderTimeConversion.toZonedEpochMilli(
                startLocalDateTime = agendaItem.startDateAndTime,
                reminderTime = agendaItem.reminderTime,
                dateTimeConversion = dateTimeConversion
            )
        )
    }

    private fun cancelScheduledNotification(agendaItemId: String) {
        scheduler.cancel(agendaItemId)
    }

    private suspend fun getAllAgendaItems(): Flow<List<AgendaItem>> {
        val reminders = db.getReminderDao().getAllReminders().map {
            it.map { reminderEntity ->
                reminderEntity.toReminder(dateTimeConversion, reminderTimeConversion)
            }
        }
        val events = db.getEventDao().getAllEvents().map {
            it.map { eventEntity ->
                eventEntity.toEvent(
                    eventRepository,
                    dateTimeConversion,
                    reminderTimeConversion,
                )
            }
        }
        val tasks = db.getTaskDao().getAllTasks().map {
            it.map { taskEntity ->
                taskEntity.toTask(dateTimeConversion, reminderTimeConversion)
            }
        }

        return combine(events, reminders, tasks) { _events, _reminders, _tasks ->
            _events + _reminders + _tasks
        }
    }
}
