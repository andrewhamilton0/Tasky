package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.agenda.SyncAgendaRequest
import com.andrew.tasky.auth.data.AuthResult

interface AgendaRepository {
    suspend fun getRemoteAgendaItems(timezone: String, time: Long): AuthResult<Unit>
    suspend fun syncAgendaItems(deleteIds: SyncAgendaRequest): AuthResult<Unit>
}
