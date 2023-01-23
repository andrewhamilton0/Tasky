package com.andrew.tasky.agenda.data.reminder

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: ReminderEntity): Long

    @Query("SELECT * FROM ReminderEntity")
    fun getReminders(): Flow<List<ReminderEntity>>

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedReminder(modifiedReminder: ModifiedReminderEntity): Long

    @Query("SELECT * FROM ModifiedReminderEntity")
    fun getModifiedReminders(): List<ModifiedReminderEntity>

    @Delete
    suspend fun deleteModifiedReminder(modifiedReminder: ModifiedReminderEntity)
}
