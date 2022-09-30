package com.andrew.tasky.data

import com.andrew.tasky.util.AgendaItemType

data class AgendaItem(
    val type: AgendaItemType,
    var isDone: Boolean,
    val title: String,
    val description: String,
    val fromTime: String,
    val fromDate: String,
)
