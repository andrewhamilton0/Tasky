package com.andrew.tasky.agenda.domain.models

import java.io.Serializable
import java.time.LocalDate

data class CalendarDateItem(
    var isSelected: Boolean,
    val date: LocalDate
) : Serializable
