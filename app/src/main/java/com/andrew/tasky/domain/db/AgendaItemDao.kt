package com.andrew.tasky.domain.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrew.tasky.domain.AgendaItem

@Dao
interface AgendaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(agendaItem: AgendaItem): Long

    @Query("SELECT * FROM agendaItems")
    fun getAllAgendaItems(): LiveData<List<AgendaItem>>

    @Delete
    suspend fun deleteAgendaItem(agendaItem: AgendaItem)
}
