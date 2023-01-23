package com.andrew.tasky.agenda.data.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.LocalDateTime

@Entity
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val startDateAndTime: LocalDateTime,
    val endDateAndTime: LocalDateTime,
    val reminderTime: ReminderTime,
    val isAttendee: Boolean,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<Attendee>,
    val photos: List<EventPhoto>
)
