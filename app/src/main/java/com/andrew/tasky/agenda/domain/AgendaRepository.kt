package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.core.data.Resource
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface AgendaRepository {
    suspend fun getAgendaItemsOfDateFlow(localDate: LocalDate): Flow<List<AgendaItem>>
    suspend fun getOneTimeAgendaItemsBetweenTimes(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): List<AgendaItem>
    suspend fun updateAgendaItemCache(localDate: LocalDate)
    suspend fun syncModifiedAgendaItems(): Resource<Unit>
    suspend fun deleteAllAgendaTables()
}
