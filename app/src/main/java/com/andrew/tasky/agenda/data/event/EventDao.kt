package com.andrew.tasky.agenda.data.event

import androidx.room.*
import com.andrew.tasky.agenda.domain.models.AgendaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: AgendaItem.Event): Long

    @Query("SELECT * FROM eventdto")
    fun getEvents(): Flow<List<AgendaItem.Event>>

    @Delete
    suspend fun deleteEvent(event: AgendaItem.Event)
}
