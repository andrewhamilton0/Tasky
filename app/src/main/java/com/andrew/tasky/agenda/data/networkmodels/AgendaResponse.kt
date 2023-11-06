package com.andrew.tasky.agenda.data.networkmodels

data class AgendaResponse(
    val events: List<EventDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>
)
