package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.util.AgendaItemType
import com.andrew.tasky.auth.data.AuthResult
import java.time.LocalDateTime
import retrofit2.HttpException

class AgendaApiRepositoryImpl(
    private val api: AgendaApi
) : AgendaApiRepository {
    override suspend fun getAgendaItems(
        timezone: String,
        time: Long
    ): AuthResult<List<AgendaItem>> {
        return try {
            val response = api.getAgendaItems(timezone = timezone, time = time)
            val tasks = response.tasks.map { task ->
                AgendaItem(
                    apiId = task.id,
                    type = AgendaItemType.TASK,
                    isDone = task.isDone,
                    title = task.title,
                    description = task.description ?: "",
                    startDateAndTime = LocalDateTime.ofEpochSecond(task.time, 0, null),
                    reminderTime = ReminderTimeConversion.toEnum(
                        startTimeEpochSecond = task.time,
                        remindAtEpochSecond = task.remindAt
                    )
                )
            }
            val reminders = response.reminders.map { reminder ->
                AgendaItem(
                    apiId = reminder.id,
                    type = AgendaItemType.REMINDER,
                    isDone = reminder.isDone,
                    title = reminder.title,
                    description = reminder.description ?: "",
                    startDateAndTime = LocalDateTime.ofEpochSecond(reminder.time, 0, null),
                    reminderTime = ReminderTimeConversion.toEnum(
                        startTimeEpochSecond = reminder.time,
                        remindAtEpochSecond = reminder.remindAt
                    )
                )
            }

            val events = response.events.map { event ->
                AgendaItem(
                    apiId = event.id,
                    type = AgendaItemType.EVENT,
                    isDone = false,
                    title = event.title,
                    description = event.description,
                    startDateAndTime = LocalDateTime.ofEpochSecond(event.from, 0, null),
                    endDateAndTime = LocalDateTime.ofEpochSecond(event.to, 0, null),
                    reminderTime = ReminderTimeConversion.toEnum(
                        startTimeEpochSecond = event.from,
                        remindAtEpochSecond = event.remindAt
                    ),
                    photos = event.photos,
                    isAttendee = !event.isUserEventCreator,
                    attendees = event.attendees,
                    host = event.host
                )
            }

            val agendaItems = tasks + reminders + events
            AuthResult.Authorized(agendaItems)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse> {
        return try {
            val response = api.syncAgendaItems()
            AuthResult.Authorized(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun createEvent(): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getEvent(eventId: String): AuthResult<AgendaItem> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEvent(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEvent(): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAttendee(email: String): AuthResult<Attendee> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendee(eventId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createTask(task: AgendaItem): AuthResult<Unit> {
        return try {
            val taskRequest = Task(
                id = task.apiId,
                title = task.title,
                description = task.description,
                time = task.startDateAndTime.toEpochSecond(null),
                remindAt = ReminderTimeConversion.toEpochSecond(
                    startTimeEpochSecond = task.startDateAndTime.toEpochSecond(null),
                    reminderTime = task.reminderTime
                ),
                isDone = task.isDone ?: false
            )
            api.createTask(taskRequest)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun updateTask(task: AgendaItem): AuthResult<Unit> {
        return try {
            val taskRequest = Task(
                id = task.apiId,
                title = task.title,
                description = task.description,
                time = task.startDateAndTime.toEpochSecond(null),
                remindAt = ReminderTimeConversion.toEpochSecond(
                    startTimeEpochSecond = task.startDateAndTime.toEpochSecond(null),
                    reminderTime = task.reminderTime
                ),
                isDone = task.isDone ?: false
            )
            api.createTask(taskRequest)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun getTask(taskId: String): AuthResult<AgendaItem> {
        return try {
            val task = api.getTask(taskId)
            val taskAsAgendaItem = AgendaItem(
                apiId = task.id,
                type = AgendaItemType.TASK,
                isDone = task.isDone,
                title = task.title,
                description = task.description ?: "",
                startDateAndTime = LocalDateTime.ofEpochSecond(task.time, 0, null),
                reminderTime = ReminderTimeConversion.toEnum(
                    startTimeEpochSecond = task.time,
                    remindAtEpochSecond = task.remindAt
                )
            )
            AuthResult.Authorized(taskAsAgendaItem)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun deleteTask(taskId: String): AuthResult<Unit> {
        return try {
            api.deleteTask(taskId)
            AuthResult.Authorized()
        } catch (e: HttpException) {
            if (e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun createReminder(reminderResponse: Reminder): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminder(reminderResponse: Reminder): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(reminderId: String): AuthResult<AgendaItem> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(reminderId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }
}
