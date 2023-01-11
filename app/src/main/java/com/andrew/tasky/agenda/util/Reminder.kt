package com.andrew.tasky.agenda.util

data class Reminder(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
