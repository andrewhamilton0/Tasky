package com.andrew.tasky.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTime
import java.io.Serializable
import java.time.LocalDateTime

@Entity(
    tableName = "agendaItems"
)

data class AgendaItem(

    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    val type: AgendaItemType,
    var isDone: Boolean,
    val title: String,
    val description: String,
    val startDateAndTime: LocalDateTime,
    val endDateAndTime: LocalDateTime? = null,
    val reminderTime: ReminderTime,
    val photos: List<Photo>? = null,
    val isAttendee: Boolean? = false,
    val attendees: List<Attendee>? = null,
    val isAttending: Boolean? = true
) : Serializable
