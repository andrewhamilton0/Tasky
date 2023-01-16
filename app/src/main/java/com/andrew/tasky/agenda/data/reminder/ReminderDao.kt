package com.andrew.tasky.agenda.data.reminder

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andrew.tasky.agenda.domain.models.AgendaItem
import kotlinx.coroutines.flow.Flow

interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: AgendaItem.Reminder): Long

    @Query("SELECT * FROM ")
    fun getEvents(): Flow<List<AgendaItem.Event>>

    @Delete
    suspend fun deleteEvent(event: AgendaItem.Reminder)
}
