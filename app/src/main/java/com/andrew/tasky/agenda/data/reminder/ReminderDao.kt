package com.andrew.tasky.agenda.data.reminder

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: ReminderEntity): Long

    @Query("SELECT * FROM ReminderEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    fun getRemindersBetweenTimes(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM ReminderEntity WHERE id==:id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("DELETE FROM ReminderEntity WHERE id==:id")
    suspend fun deleteReminder(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedReminder(modifiedReminder: ModifiedReminderEntity): Long

    @Query("SELECT * FROM ModifiedReminderEntity")
    suspend fun getModifiedReminders(): List<ModifiedReminderEntity>

    @Query("DELETE FROM ModifiedReminderEntity WHERE id==:id")
    suspend fun deleteModifiedReminderById(id: String): Int
}
