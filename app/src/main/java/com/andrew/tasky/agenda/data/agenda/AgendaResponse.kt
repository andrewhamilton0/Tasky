package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.networkModels.EventDto
import com.andrew.tasky.agenda.data.networkModels.ReminderDto
import com.andrew.tasky.agenda.data.networkModels.TaskDto

data class AgendaResponse(
    val events: List<EventDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>
)
