package com.andrew.tasky.domain

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
    val reminderTime: ReminderTimes
) : Serializable
