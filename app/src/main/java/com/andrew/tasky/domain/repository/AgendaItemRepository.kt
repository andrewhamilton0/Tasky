package com.andrew.tasky.domain.repository

import androidx.lifecycle.LiveData
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.db.AgendaItemDatabase
import javax.inject.Inject

class AgendaItemRepository @Inject constructor(
    private val db: AgendaItemDatabase
) {
    suspend fun upsert(agendaItem: AgendaItem) {
        db.getAgendaItemDao().upsert(agendaItem)
    }

    fun getAgendaItems(): LiveData<List<AgendaItem>> {
        return db.getAgendaItemDao().getAllAgendaItems()
    }

    suspend fun deleteAgendaItem(agendaItem: AgendaItem) {
        db.getAgendaItemDao().deleteAgendaItem(agendaItem)
    }
}
