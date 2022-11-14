package com.andrew.tasky.domain.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrew.tasky.domain.AgendaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(agendaItem: AgendaItem): Long

    @Query("SELECT * FROM agendaItems")
    fun getAllAgendaItems(): Flow<List<AgendaItem>>

    @Delete
    suspend fun deleteAgendaItem(agendaItem: AgendaItem)
}
