package com.andrew.tasky.agenda.data.reminder

import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.data.util.toReminderDto
import com.andrew.tasky.agenda.data.util.toReminderEntity
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: ReminderApi
) : ReminderRepository {

    override suspend fun createReminder(reminder: AgendaItem.Reminder) {
        db.getReminderDao().upsertReminder(reminder.toReminderEntity())
        val result = getAuthResult { api.createReminder(reminder.toReminderDto()) }
        if (result !is AuthResult.Authorized) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminder.id,
                    modifiedType = ModifiedType.CREATE
                )
            )
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder) {
        db.getReminderDao().upsertReminder(reminder.toReminderEntity())
        val result = getAuthResult { api.updateReminder(reminder.toReminderDto()) }
        if (result !is AuthResult.Authorized) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminder.id,
                    modifiedType = ModifiedType.UPDATE
                )
            )
        }
    }

    override suspend fun getReminder(reminderId: String): AuthResult<AgendaItem.Reminder> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(reminder: AgendaItem.Reminder) {
        db.getReminderDao().deleteReminder(reminder.toReminderEntity())
        val result = getAuthResult { api.deleteReminder(reminder.id) }
        if (result !is AuthResult.Authorized) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminder.id,
                    modifiedType = ModifiedType.DELETE
                )
            )
        }
    }
}
