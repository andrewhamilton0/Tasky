package com.andrew.tasky.agenda.data.task

import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

class TaskRepositoryImpl() : TaskRepository {
    override suspend fun createTask(task: AgendaItem.Task): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: AgendaItem.Task): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): AuthResult<AgendaItem.Task> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }
}
