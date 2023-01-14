package com.andrew.tasky.agenda.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andrew.tasky.agenda.util.ReminderTime
import java.io.Serializable
import java.time.LocalDateTime

sealed interface AgendaItem : Serializable {

    @Entity
    data class Event(
        @PrimaryKey(autoGenerate = true) var id: String? = null,
        var isDone: Boolean,
        val title: String,
        val description: String,
        val startDateAndTime: LocalDateTime,
        val endDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime,
        val photos: List<EventPhoto>? = emptyList(),
        val isAttendee: Boolean,
        val attendees: List<Attendee>? = emptyList(),
        val host: String
    ) : AgendaItem

    @Entity
    data class Task(
        @PrimaryKey(autoGenerate = true) var id: String? = null,
        var isDone: Boolean,
        val title: String,
        val description: String,
        val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem

    @Entity
    data class Reminder(
        @PrimaryKey(autoGenerate = true) var id: String? = null,
        var isDone: Boolean,
        val title: String,
        val description: String,
        val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem
}
