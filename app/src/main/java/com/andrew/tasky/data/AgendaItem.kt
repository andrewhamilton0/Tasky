package com.andrew.tasky.data

import com.andrew.tasky.util.AgendaItemType
import java.time.LocalDateTime

data class AgendaItem(
    val type: AgendaItemType,
    var isDone: Boolean,
    val title: String,
    val description: String,
    val startDateAndTime: LocalDateTime,
    val endDateAndTime: LocalDateTime? = null,
)
