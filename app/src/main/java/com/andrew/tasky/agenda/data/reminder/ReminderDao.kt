package com.andrew.tasky.agenda.data.reminder

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: ReminderEntity): Long

    @Query("SELECT * FROM ReminderEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    fun getRemindersOfDate(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM ReminderEntity WHERE id==:id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedReminder(modifiedReminder: ModifiedReminderEntity): Long

    @Query("SELECT * FROM ModifiedReminderEntity")
    suspend fun getModifiedReminders(): List<ModifiedReminderEntity>

    @Delete
    suspend fun deleteModifiedReminder(modifiedReminder: ModifiedReminderEntity)

    @Query("DELETE FROM ModifiedReminderEntity WHERE id==:id")
    suspend fun deleteModifiedReminderById(id: String): Int

    @Query("DELETE FROM ReminderEntity")
    suspend fun deleteReminderDb()

    @Query("DELETE FROM ModifiedReminderEntity")
    suspend fun deleteModifiedReminderDb()
}
