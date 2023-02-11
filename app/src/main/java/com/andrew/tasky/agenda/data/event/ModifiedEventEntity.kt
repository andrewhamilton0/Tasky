package com.andrew.tasky.agenda.data.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.data.util.ModifiedType

@Entity
data class ModifiedEventEntity(
    @PrimaryKey val id: String,
    val modifiedType: ModifiedType
)
