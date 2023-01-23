package com.andrew.tasky.agenda.data.reminder

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.data.util.ModifiedType

@Entity
data class ModifiedReminderEntity(
    @PrimaryKey val id: String,
    val modifiedType: ModifiedType
)
