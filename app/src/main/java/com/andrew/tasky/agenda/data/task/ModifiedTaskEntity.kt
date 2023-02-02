package com.andrew.tasky.agenda.data.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.data.util.ModifiedType

@Entity
data class ModifiedTaskEntity(
    @PrimaryKey val id: String,
    val modifiedType: ModifiedType
)
