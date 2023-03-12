package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class AgendaItem(open val startDateAndTime: LocalDateTime, open val id: String) {

    @Parcelize
    data class Event(
        override val id: String,
        val isDone: Boolean,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val endDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime,
        val photos: @RawValue List<EventPhoto>,
        val isCreator: Boolean,
        val attendees: @RawValue List<Attendee>,
        val host: String?,
        val deletedPhotoKeys: List<String> = emptyList(),
        val isGoing: Boolean
    ) : AgendaItem(startDateAndTime = startDateAndTime, id = id), Parcelable

    @Parcelize
    data class Task(
        override val id: String,
        val isDone: Boolean = false,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem(startDateAndTime = startDateAndTime, id = id), Parcelable

    @Parcelize
    data class Reminder(
        override val id: String,
        val isDone: Boolean = false,
        val title: String,
        val description: String,
        override val startDateAndTime: LocalDateTime,
        val reminderTime: ReminderTime
    ) : AgendaItem(startDateAndTime = startDateAndTime, id = id), Parcelable
}
