package com.andrew.tasky.agenda.data

data class AgendaResponse(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
