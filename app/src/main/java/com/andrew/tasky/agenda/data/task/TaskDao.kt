package com.andrew.tasky.agenda.data.task

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity): Long

    @Query("SELECT * FROM TaskEntity WHERE time BETWEEN :startEpochMilli AND :endEpochMilli")
    fun getTasksBetweenTimes(
        startEpochMilli: Long,
        endEpochMilli: Long
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM TaskEntity WHERE id==:id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT * FROM TaskEntity")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("DELETE FROM TaskEntity WHERE id==:id")
    suspend fun deleteTask(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModifiedTask(modifiedTask: ModifiedTaskEntity): Long

    @Query("SELECT * FROM ModifiedTaskEntity")
    fun getModifiedTasks(): Flow<List<ModifiedTaskEntity>>

    @Query("DELETE FROM ModifiedTaskEntity WHERE id==:id")
    suspend fun deleteModifiedTaskById(id: String): Int
}
