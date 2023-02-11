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
    fun getEventsOfDate(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<EventEntity>>

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedEvent(modifiedEventEntity: ModifiedEventEntity)
}
