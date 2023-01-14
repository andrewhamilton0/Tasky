package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.agenda.SyncAgendaResponse
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface AgendaRepository {
    suspend fun getAgendaItems(timezone: String, time: Long): AuthResult<List<AgendaItem>>
    suspend fun syncAgendaItems(): AuthResult<SyncAgendaResponse>
}
