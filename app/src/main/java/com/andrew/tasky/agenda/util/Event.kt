package com.andrew.tasky.agenda.util

import android.provider.CalendarContract.Attendees
import com.andrew.tasky.agenda.domain.models.EventPhoto

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<Attendees>,
    val photos: List<EventPhoto>
)
