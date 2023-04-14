package com.andrew.tasky.agenda.data.event

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvent(event: EventEntity)

    @Query(
        "SELECT * FROM EventEntity WHERE startDateAndTime BETWEEN " +
            ":startEpochMilli AND :endEpochMilli"
    )
    fun getEventsBetweenTimes(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE id==:id")
    suspend fun getEventById(id: String): EventEntity?

    @Query("DELETE FROM EventEntity WHERE id==:id")
    suspend fun deleteEvent(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedEvent(modifiedEventEntity: ModifiedEventEntity)

    @Query("SELECT * FROM ModifiedEventEntity WHERE id==:id")
    suspend fun getModifiedEventById(id: String): ModifiedEventEntity?

    @Query("SELECT * FROM ModifiedEventEntity")
    suspend fun getModifiedEvents(): List<ModifiedEventEntity>

    @Query("DELETE FROM ModifiedEventEntity WHERE id==:id")
    suspend fun deleteModifiedEventById(id: String): Int
}
