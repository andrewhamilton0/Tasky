package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

interface ReminderRepository {
    suspend fun createReminder(reminder: AgendaItem.Reminder)
    suspend fun updateReminder(reminder: AgendaItem.Reminder): AuthResult<Unit>
    suspend fun getReminder(reminderId: String): AuthResult<AgendaItem.Reminder>
    suspend fun deleteReminder(reminderId: String): AuthResult<Unit>
}
