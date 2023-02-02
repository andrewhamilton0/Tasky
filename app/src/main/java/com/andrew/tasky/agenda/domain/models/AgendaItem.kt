package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class AgendaItem(open val startDateAndTime: LocalDateTime) {

    @Parcelize
    data class Event(
        val id: String? = null,
        val isDone: Boolean,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val endDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime,
        val photos: @RawValue List<EventPhoto>? = emptyList(),
        val isAttendee: Boolean,
        val attendees: @RawValue List<Attendee>? = emptyList(),
        val host: String
    ) : AgendaItem(startDateAndTime = startDateAndTime), Parcelable

    @Parcelize
    data class Task(
        val id: String,
        val isDone: Boolean = false,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem(startDateAndTime = startDateAndTime), Parcelable

    @Parcelize
    data class Reminder(
        val id: String,
        val isDone: Boolean = false,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem(startDateAndTime = startDateAndTime), Parcelable
}
