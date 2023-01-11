package com.andrew.tasky.agenda.data

import com.andrew.tasky.agenda.util.Event
import com.andrew.tasky.agenda.util.Reminder
import com.andrew.tasky.agenda.util.Task

data class AgendaResponse(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
