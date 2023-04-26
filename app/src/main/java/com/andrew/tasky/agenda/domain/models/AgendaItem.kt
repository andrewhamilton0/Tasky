package com.andrew.tasky.agenda.domain.models

import android.os.Parcelable
import com.andrew.tasky.agenda.util.ReminderTime
import java.time.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed class AgendaItem(
    open val startDateAndTime: LocalDateTime,
    open val id: String,
    open val title: String,
    open val description: String,
    open val reminderTime: ReminderTime
) {

    @Parcelize
    data class Event(
        override val id: String,
        val isDone: Boolean,
        override val title: String,
        override val description: String,
        override val startDateAndTime: LocalDateTime,
        val endDateAndTime: LocalDateTime,
        override val reminderTime: ReminderTime,
        val photos: @RawValue List<EventPhoto>,
        val isCreator: Boolean,
        val attendees: @RawValue List<Attendee>,
        val host: String?,
        val deletedPhotos: @RawValue List<EventPhoto> = emptyList(),
        val isGoing: Boolean
    ) : AgendaItem(
        startDateAndTime = startDateAndTime,
        id = id,
        title = title,
        description = description,
        reminderTime = reminderTime
    ),
        Parcelable

    @Parcelize
    data class Task(
        override val id: String,
        val isDone: Boolean = false,
        override val title: String,
        override val description: String,
        override val startDateAndTime: LocalDateTime,
        override val reminderTime: ReminderTime
    ) : AgendaItem(
        startDateAndTime = startDateAndTime,
        id = id,
        title = title,
        description = description,
        reminderTime = reminderTime
    ),
        Parcelable

    @Parcelize
    data class Reminder(
        override val id: String,
        val isDone: Boolean = false,
        override val title: String,
        override val description: String,
        override val startDateAndTime: LocalDateTime,
        override val reminderTime: ReminderTime
    ) : AgendaItem(
        startDateAndTime = startDateAndTime,
        id = id,
        title = title,
        description = description,
        reminderTime = reminderTime
    ),
        Parcelable
}
