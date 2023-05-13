package com.andrew.tasky.agenda.data.agenda.notifications

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersistedNotifEntity(
    @PrimaryKey val agendaId: String
)
