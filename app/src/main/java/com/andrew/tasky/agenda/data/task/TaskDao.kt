package com.andrew.tasky.agenda.data.task

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity): Long

    @Query("SELECT * FROM TaskEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    fun getTasksOfDateFlow(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM TaskEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    suspend fun getTasksOfDate(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): List<TaskEntity>

    @Query("SELECT * FROM TaskEntity WHERE id==:id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedTask(modifiedTask: ModifiedTaskEntity): Long

    @Query("SELECT * FROM ModifiedTaskEntity")
    suspend fun getModifiedTasks(): List<ModifiedTaskEntity>

    @Delete
    suspend fun deleteModifiedTask(modifiedTask: ModifiedTaskEntity)

    @Query("DELETE FROM ModifiedTaskEntity WHERE id==:id")
    suspend fun deleteModifiedTaskById(id: String): Int
}
