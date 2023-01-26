package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    suspend fun updateAgendaOfDate(date: LocalDate)
    fun getAgendaItems(): Flow<List<AgendaItem>>
    suspend fun syncAgendaItems()
}
