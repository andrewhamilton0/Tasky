package com.andrew.tasky.agenda.data.agenda

import com.andrew.tasky.agenda.data.event.EventDto
import com.andrew.tasky.agenda.data.reminder.ReminderDto
import com.andrew.tasky.agenda.data.task.TaskDto

data class AgendaResponse(
    val eventDtos: List<EventDto>,
    val taskDtos: List<TaskDto>,
    val reminderDtos: List<ReminderDto>
)
