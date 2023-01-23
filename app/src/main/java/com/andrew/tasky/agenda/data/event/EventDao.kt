package com.andrew.tasky.agenda.data.event

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: EventEntity): Long

    @Query("SELECT * FROM evententity")
    fun getEvents(): Flow<List<EventEntity>>

    @Delete
    suspend fun deleteEvent(event: EventEntity)
}
