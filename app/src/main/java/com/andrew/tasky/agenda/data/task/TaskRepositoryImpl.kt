package com.andrew.tasky.agenda.data.task

import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: TaskApi
) : TaskRepository {

    override suspend fun createTask(task: AgendaItem.Task) {
        db.getTaskDao().upsertTask(task.toTaskEntity())
        val result = getAuthResult { api.createTask(task.toTaskDto()) }
        if (result !is AuthResult.Authorized) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = task.id,
                    modifiedType = ModifiedType.CREATE
                )
            )
            Log.e("create task", "sent to modified tasks")
        }
    }

    override suspend fun updateTask(task: AgendaItem.Task) {
        db.getTaskDao().upsertTask(task.toTaskEntity())
        val result = getAuthResult { api.updateTask(task.toTaskDto()) }
        if (result !is AuthResult.Authorized) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = task.id,
                    modifiedType = ModifiedType.UPDATE
                )
            )
        }
    }

    override suspend fun deleteTask(task: AgendaItem.Task) {
        db.getTaskDao().deleteTask(task.toTaskEntity())
        val result = getAuthResult { api.deleteTask(task.id) }
        if (result !is AuthResult.Authorized) {
            db.getTaskDao().upsertModifiedTask(
                ModifiedTaskEntity(
                    id = task.id,
                    modifiedType = ModifiedType.DELETE
                )
            )
        }
    }

    override suspend fun uploadCreateAndUpdateModifiedTasks() {
        val modifiedTasks = db.getTaskDao().getModifiedTasks().groupBy {
            it.modifiedType
        }

        val createTasksDtos = modifiedTasks[ModifiedType.CREATE]?.map {
            db.getTaskDao().getTaskById(it.id)?.toTaskDto()
        }
        createTasksDtos?.map { createTasksDto ->
            when (getAuthResult { createTasksDto?.let { api.createTask(it) } }) {
                is AuthResult.Authorized -> {
                    createTasksDto?.let {
                        db.getTaskDao().deleteModifiedTaskById(it.id)
                    }
                }
                is AuthResult.Unauthorized -> Log.e(
                    "SyncAgendaItem",
                    "Unauthorized, could not create task"
                )
                is AuthResult.UnknownError -> Log.e(
                    "SyncAgendaItem",
                    "Unknown Error, could not create task"
                )
            }
        }

        val updateTasksDtos = modifiedTasks[ModifiedType.UPDATE]?.map {
            db.getTaskDao().getTaskById(it.id)?.toTaskDto()
        }
        updateTasksDtos?.map { updateTasksDto ->
            when (getAuthResult { updateTasksDto?.let { api.updateTask(it) } }) {
                is AuthResult.Authorized -> {
                    updateTasksDto?.let {
                        db.getTaskDao().deleteModifiedTaskById(it.id)
                    }
                }
                is AuthResult.Unauthorized -> Log.e(
                    "SyncAgendaItem",
                    "Unauthorized, could not update task"
                )
                is AuthResult.UnknownError -> Log.e(
                    "SyncAgendaItem",
                    "Unknown Error, could not update task"
                )
            }
        }
    }
}
