package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.util.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import java.time.LocalDateTime
import retrofit2.HttpException

class AgendaRepositoryImpl(
    private val api: AgendaApi
) : AgendaRepository {
    override suspend fun getAgendaItems(
        timezone: String,
        time: Long
    ): AuthResult<List<AgendaItem>> {
        return try {
            val response = api.getAgendaItems(timezone = timezone, time = time)
            val tasks = response.taskDtos.map { task ->
                AgendaItem.Task(
                    id = task.id.toInt(),
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
            val reminders = response.reminderDtos.map { reminder ->
                AgendaItem.Reminder(
                    id = reminder.id.toInt(),
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
            val events = response.eventDtos.map { event ->
                AgendaItem.Event(
                    id = event.id.toInt(),
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

            val agendaItems = tasks + events + reminders
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
}
