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
    override suspend fun getAgendaItems(timezone: String, time: Long
    ): AuthResult<List<AgendaItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createTask(task: AgendaItem): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: AgendaItem): AuthResult<Unit> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}
