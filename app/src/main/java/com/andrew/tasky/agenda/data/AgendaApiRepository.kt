package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface AgendaApiRepository {
    suspend fun getAgendaItems(timezone: String, time: Long): AuthResult<List<AgendaItem>>
    suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse>
    suspend fun createTask(task: AgendaItem): AuthResult<Unit>
    suspend fun updateTask(task: AgendaItem): AuthResult<Unit>
    suspend fun getTask(taskId: String): AuthResult<AgendaItem>
    suspend fun deleteTask(taskId: String): AuthResult<Unit>
}
