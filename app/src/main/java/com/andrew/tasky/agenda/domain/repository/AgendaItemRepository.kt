package com.andrew.tasky.agenda.domain.repository

import com.andrew.tasky.agenda.domain.db.AgendaItemDatabase
import com.andrew.tasky.agenda.domain.models.AgendaItem
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class AgendaItemRepository @Inject constructor(
    private val db: AgendaItemDatabase
) {
    suspend fun upsert(agendaItem: AgendaItem) {
        db.getAgendaItemDao().upsert(agendaItem)
    }

    fun getAgendaItems(): Flow<List<AgendaItem>> {
        return db.getAgendaItemDao().getAllAgendaItems()
    }

    suspend fun deleteAgendaItem(agendaItem: AgendaItem) {
        db.getAgendaItemDao().deleteAgendaItem(agendaItem)
    }
}
