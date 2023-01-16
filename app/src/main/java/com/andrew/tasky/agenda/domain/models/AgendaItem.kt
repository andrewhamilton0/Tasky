package com.andrew.tasky.agenda.domain.models

import com.andrew.tasky.agenda.util.ReminderTime
import java.io.Serializable
import java.time.LocalDateTime

sealed interface AgendaItem : Serializable {

    data class Event(
        val id: String? = null,
        val isDone: Boolean,
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

    data class Task(
        val id: String? = null,
        val isDone: Boolean,
        val title: String,
        val description: String,
        val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem

    data class Reminder(
        val id: String? = null,
        val isDone: Boolean,
        val title: String,
        val description: String,
        val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem
}
