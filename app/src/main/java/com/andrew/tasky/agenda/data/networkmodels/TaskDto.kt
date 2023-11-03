package com.andrew.tasky.agenda.data.networkmodels

data class TaskDto(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)
