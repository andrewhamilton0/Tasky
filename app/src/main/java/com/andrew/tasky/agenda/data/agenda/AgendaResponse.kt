package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.event.EventDto
import com.andrew.tasky.agenda.data.reminder.ReminderDto
import com.andrew.tasky.agenda.data.task.TaskDto

data class AgendaResponse(
    val events: List<EventDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>
)
