package com.andrew.tasky.agenda.data.networkmodels

data class ReminderDto(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long
)
