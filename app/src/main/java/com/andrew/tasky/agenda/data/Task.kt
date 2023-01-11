package com.andrew.tasky.agenda.data

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
