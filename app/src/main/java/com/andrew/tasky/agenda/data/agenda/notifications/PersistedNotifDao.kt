package com.andrew.tasky.agenda.data.agenda.notifications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersistedNotifDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersistedNotif(persistedNotifEntity: PersistedNotifEntity)

    @Query("SELECT * FROM PersistedNotifEntity")
    fun getAllPersistedNotifs(): Flow<List<PersistedNotifEntity>>

    @Query("DELETE FROM PersistedNotifEntity")
    suspend fun deleteAllPersistedNotifs()
}
