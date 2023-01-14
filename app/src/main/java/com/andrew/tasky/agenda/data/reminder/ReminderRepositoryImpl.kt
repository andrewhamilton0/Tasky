package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult

class ReminderRepositoryImpl() : ReminderRepository {

    override suspend fun createReminder(reminderDto: ReminderDto): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminder(reminderDto: ReminderDto): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(reminderId: String): AuthResult<AgendaItem> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(reminderId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }
}
