package com.andrew.tasky.agenda.data.reminder

import android.content.Context
import android.util.Log
import com.andrew.tasky.agenda.data.database.AgendaDatabase
import com.andrew.tasky.agenda.data.util.ModifiedType
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.ReminderTimeConversion
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.data.Resource
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val db: AgendaDatabase,
    private val api: ReminderApi,
    private val appContext: Context,
    private val scheduler: AgendaNotificationScheduler
) : ReminderRepository {

    override suspend fun createReminder(reminder: AgendaItem.Reminder) {
        scheduleNotification(reminder)
        db.getReminderDao().upsertReminder(reminder.toReminderEntity())
        val result = getResourceResult { api.createReminder(reminder.toReminderDto()) }
        if (result is Resource.Error) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminder.id,
                    modifiedType = ModifiedType.CREATE
                )
            )
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder) {
        scheduleNotification(reminder)
        db.getReminderDao().upsertReminder(reminder.toReminderEntity())
        val result = getResourceResult { api.updateReminder(reminder.toReminderDto()) }
        if (result is Resource.Error) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminder.id,
                    modifiedType = ModifiedType.UPDATE
                )
            )
        }
    }

    override suspend fun getReminder(reminderId: String): AgendaItem.Reminder? {
        return db.getReminderDao().getReminderById(reminderId)?.toReminder()
    }

    override suspend fun deleteReminder(reminderId: String) {
        cancelScheduledNotification(reminderId)
        db.getReminderDao().deleteReminder(reminderId)
        val result = getResourceResult { api.deleteReminder(reminderId) }
        if (result is Resource.Error) {
            db.getReminderDao().upsertModifiedReminder(
                ModifiedReminderEntity(
                    id = reminderId,
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
        createRemindersDtos?.forEach { createRemindersDto ->
            if (createRemindersDto != null) {
                val result = getResourceResult { api.createReminder(createRemindersDto) }
                when (result) {
                    is Resource.Error -> {
                        Log.e(
                            "uploadCreateAndUpdateModifiedReminders error",
                            result.message?.asString(appContext) ?: "Unknown Error"
                        )
                    }
                    is Resource.Success -> {
                        db.getReminderDao().deleteModifiedReminderById(createRemindersDto.id)
                    }
                }
            }
        }

        val updateRemindersDtos = modifiedReminders[ModifiedType.UPDATE]?.map {
            db.getReminderDao().getReminderById(it.id)?.toReminderDto()
        }
        updateRemindersDtos?.forEach { updateRemindersDto ->
            if (updateRemindersDto != null) {
                val result = getResourceResult { api.updateReminder(updateRemindersDto) }
                when (result) {
                    is Resource.Error -> {
                        Log.e(
                            "uploadCreateAndUpdateModifiedReminders error",
                            result.message?.asString(appContext) ?: "Unknown Error"
                        )
                    }
                    is Resource.Success -> {
                        db.getReminderDao().deleteModifiedReminderById(updateRemindersDto.id)
                    }
                }
            }
        }
    }

    private fun scheduleNotification(reminder: AgendaItem.Reminder) {
        scheduler.schedule(
            agendaId = reminder.id,
            time = ReminderTimeConversion.toZonedEpochMilli(
                startLocalDateTime = reminder.startDateAndTime,
                reminderTime = reminder.reminderTime
            )
        )
    }

    private fun cancelScheduledNotification(reminderId: String) {
        scheduler.cancel(reminderId)
    }
}
