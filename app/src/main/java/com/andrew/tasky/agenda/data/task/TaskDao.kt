package com.andrew.tasky.agenda.data.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity): Long

    @Query("SELECT * FROM TaskEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    fun getTasksOfDate(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<TaskEntity>>

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
