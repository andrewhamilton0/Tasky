package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface TaskRepository {
    suspend fun createTask(task: AgendaItem): AuthResult<Unit>
    suspend fun updateTask(task: AgendaItem): AuthResult<Unit>
    suspend fun getTask(taskId: String): AuthResult<AgendaItem>
    suspend fun deleteTask(taskId: String): AuthResult<Unit>
}
