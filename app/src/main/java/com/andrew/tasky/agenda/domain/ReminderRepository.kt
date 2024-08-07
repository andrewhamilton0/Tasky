package com.andrew.tasky.agenda.domain

import com.andrew.tasky.agenda.domain.models.AgendaItem

interface ReminderRepository {
    suspend fun createReminder(reminder: AgendaItem.Reminder)
    suspend fun toggleIsDone(reminderId: String)
    suspend fun updateReminder(reminder: AgendaItem.Reminder)
    suspend fun getReminder(reminderId: String): AgendaItem.Reminder?
    suspend fun deleteReminder(reminderId: String)
    suspend fun uploadCreateAndUpdateModifiedReminders()
}
