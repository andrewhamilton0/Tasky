package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface TaskRepository {
    suspend fun createTask(task: AgendaItem.Task): AuthResult<Unit>
    suspend fun updateTask(task: AgendaItem.Task): AuthResult<Unit>
    suspend fun getTask(taskId: String): AuthResult<AgendaItem.Task>
    suspend fun deleteTask(taskId: String): AuthResult<Unit>
}
