package com.andrew.tasky.agenda.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.util.AgendaItemType
import com.andrew.tasky.agenda.util.ReminderTime
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
    val photos: List<EventPhoto>? = emptyList(),
    val isAttendee: Boolean? = false,
    val attendees: List<Attendee>? = emptyList(),
    val isAttending: Boolean? = true
) : Serializable
