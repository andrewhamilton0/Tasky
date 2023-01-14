package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.data.reminder.ReminderDto
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface ReminderRepository {
    suspend fun createReminder(reminderDto: ReminderDto): AuthResult<Unit>
    suspend fun updateReminder(reminderDto: ReminderDto): AuthResult<Unit>
    suspend fun getReminder(reminderId: String): AuthResult<AgendaItem>
    suspend fun deleteReminder(reminderId: String): AuthResult<Unit>
}
