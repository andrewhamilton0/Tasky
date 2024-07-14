package com.andrew.tasky.agenda.data.task

import android.content.Context
import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler
import com.andrew.tasky.agenda.domain.DateTimeConversion
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.data.Resource
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class TaskRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: TaskApi,
    private val appContext: Context,
    private val scheduler: AgendaNotificationScheduler,
    private val dateTimeConversion: DateTimeConversion,
    private val reminderTimeConversion: ReminderTimeConversion
) : TaskRepository {

    override suspend fun createTask(task: AgendaItem.Task) {
        scheduleNotification(task)
        db.getTaskDao().upsertTask(
            task.toTaskEntity(
                dateTimeConversion = dateTimeConversion,
                reminderTimeConversion = reminderTimeConversion
            )
        )
        val result = getResourceResult {
            api.createTask(
                task.toTaskDto(
                    dateTimeConversion = dateTimeConversion,
                    reminderTimeConversion = reminderTimeConversion
                )
            )
        }
        if (result is Resource.Error) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = task.id,
                    modifiedType = ModifiedType.CREATE
                )
            )
            Log.e(
                "createTask error",
                result.message?.asString(appContext) ?: "unknown error"
            )
        }
    }

    override suspend fun updateTask(task: AgendaItem.Task) {
        scheduleNotification(task)
        db.getTaskDao().upsertTask(
            task.toTaskEntity(
                dateTimeConversion = dateTimeConversion,
                reminderTimeConversion = reminderTimeConversion
            )
        )
        val result = getResourceResult {
            api.updateTask(
                task.toTaskDto(
                    dateTimeConversion = dateTimeConversion,
                    reminderTimeConversion = reminderTimeConversion
                )
            )
        }
        if (result is Resource.Error) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = task.id,
                    modifiedType = ModifiedType.UPDATE
                )
            )
            Log.e(
                "updateTask error",
                result.message?.asString(appContext) ?: "unknown error"
            )
        }
    }

    override suspend fun toggleIsDone(taskId: String) {
        val task = db.getTaskDao().getTaskById(taskId)
        task?.let {
            db.getTaskDao().upsertTask(it.copy(isDone = !it.isDone))
        }
    }

    override suspend fun getTask(taskId: String): AgendaItem.Task? {
        return db.getTaskDao().getTaskById(taskId)?.toTask(
            dateTimeConversion = dateTimeConversion,
            reminderTimeConversion = reminderTimeConversion
        )
    }

    override suspend fun deleteTask(taskId: String) {
        cancelScheduledNotification(taskId)
        db.getTaskDao().deleteTask(taskId)
        val result = getResourceResult { api.deleteTask(taskId) }
        if (result is Resource.Error) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = taskId,
                    modifiedType = ModifiedType.DELETE
                )
            )
            Log.e(
                "deleteTask error",
                result.message?.asString(appContext) ?: "unknown error"
            )
        }
    }

    override suspend fun uploadCreateAndUpdateModifiedTasks() {
        val modifiedTasks = db.getTaskDao().getModifiedTasks().first().groupBy {
            it.modifiedType
        }

        val createTasksDtos = modifiedTasks[ModifiedType.CREATE]?.map {
            db.getTaskDao().getTaskById(it.id)?.toTaskDto()
        }
        createTasksDtos?.map { createTasksDto ->
            if (createTasksDto != null) {
                val result = getResourceResult { api.createTask(createTasksDto) }
                when (result) {
                    is Resource.Error -> {
                        Log.e(
                            "uploadCreateAndUpdateModifiedTasks create error",
                            result.message?.asString(appContext) ?: "Unknown Error"
                        )
                    }
                    is Resource.Success -> {
                        db.getTaskDao().deleteModifiedTaskById(createTasksDto.id)
                    }
                }
            }
        }

        val updateTasksDtos = modifiedTasks[ModifiedType.UPDATE]?.map {
            db.getTaskDao().getTaskById(it.id)?.toTaskDto()
        }
        updateTasksDtos?.map { updateTasksDto ->
            if (updateTasksDto != null) {
                val result = getResourceResult { api.updateTask(updateTasksDto) }
                when (result) {
                    is Resource.Error -> {
                        Log.e(
                            "uploadCreateAndUpdateModifiedTasks update error",
                            result.message?.asString(appContext) ?: "Unknown Error"
                        )
                    }
                    is Resource.Success -> {
                        db.getTaskDao().deleteModifiedTaskById(updateTasksDto.id)
                    }
                }
            }
        }
    }

    private fun scheduleNotification(task: AgendaItem.Task) {
        scheduler.schedule(
            agendaId = task.id,
            time = reminderTimeConversion.toZonedEpochMilli(
                startLocalDateTime = task.startDateAndTime,
                reminderTime = task.reminderTime,
                dateTimeConversion = dateTimeConversion
            )
        )
    }

    private fun cancelScheduledNotification(taskId: String) {
        scheduler.cancel(taskId)
    }
}
