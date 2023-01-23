package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    suspend fun updateAgendaOfDate(time: Long, timezone: String)
    fun getAgendaItems(): Flow<List<AgendaItem>>
    suspend fun syncAgendaItems(): AuthResult<Unit>
}
