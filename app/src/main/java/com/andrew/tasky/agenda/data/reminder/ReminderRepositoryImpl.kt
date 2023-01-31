package com.andrew.tasky.agenda.data.reminder

import android.util.Log
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
            Log.e("create reminder", "sent to modified reminders")
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

    override suspend fun uploadCreateAndUpdateModifiedReminders() {

        val modifiedReminders = db.getReminderDao().getModifiedReminders().groupBy {
            it.modifiedType
        }

        val createRemindersDtos = modifiedReminders[ModifiedType.CREATE]?.map {
            db.getReminderDao().getReminderById(it.id)?.toReminderDto()
        }
        createRemindersDtos?.map { createRemindersDto ->
            when (getAuthResult { createRemindersDto?.let { api.createReminder(it) } }) {
                is AuthResult.Authorized -> {
                    createRemindersDto?.let {
                        db.getReminderDao().deleteModifiedReminderById(it.id)
                    }
                }
                is AuthResult.Unauthorized -> Log.e(
                    "SyncAgendaItem",
                    "Unauthorized, could not create reminder"
                )
                is AuthResult.UnknownError -> Log.e(
                    "SyncAgendaItem",
                    "Unknown Error, could not create reminder"
                )
            }
        }

        val updateRemindersDtos = modifiedReminders[ModifiedType.UPDATE]?.map {
            db.getReminderDao().getReminderById(it.id)?.toReminderDto()
        }
        updateRemindersDtos?.map { updateRemindersDto ->
            when (getAuthResult { updateRemindersDto?.let { api.updateReminder(it) } }) {
                is AuthResult.Authorized -> {
                    updateRemindersDto?.let {
                        db.getReminderDao().deleteModifiedReminderById(it.id)
                    }
                }
                is AuthResult.Unauthorized -> Log.e(
                    "SyncAgendaItem",
                    "Unauthorized, could not update reminder"
                )
                is AuthResult.UnknownError -> Log.e(
                    "SyncAgendaItem",
                    "Unknown Error, could not update reminder"
                )
            }
        }
    }
}
