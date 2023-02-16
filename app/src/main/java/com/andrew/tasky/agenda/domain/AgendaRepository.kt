package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    suspend fun getAgendaItems(localDate: LocalDate): Flow<List<AgendaItem>>
    suspend fun updateAgendaItemCache(localDate: LocalDate)
    suspend fun syncModifiedAgendaItems()
    suspend fun deleteAllAgendaTables()
}
