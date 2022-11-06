package com.andrew.tasky.domain

import android.net.Uri
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTimes
import java.io.Serializable
import java.time.LocalDateTime

data class AgendaItem(
    val type: AgendaItemType,
    var isDone: Boolean,
    val title: String,
    val description: String,
    val startDateAndTime: LocalDateTime,
    val endDateAndTime: LocalDateTime? = null,
    val reminderTime: ReminderTimes,
    val photos: List<Uri>? = null,
    val isAttendee: Boolean? = false,
    val attendees: List<String>? = null,
    val isAttending: Boolean? = true
) : Serializable
