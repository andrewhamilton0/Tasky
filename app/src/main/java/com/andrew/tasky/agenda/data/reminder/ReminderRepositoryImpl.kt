package com.andrew.tasky.agenda.data.reminder

import android.app.Application
import androidx.work.*
import com.andrew.tasky.agenda.data.util.toReminderDto
import com.andrew.tasky.agenda.data.util.toReminderEntity
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.auth.data.AuthResult
import com.google.gson.Gson
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val appContext: Application,
    private val db: ReminderDatabase
) : ReminderRepository {

    override suspend fun createReminder(reminder: AgendaItem.Reminder) {
        db.getReminderDao().upsert(reminder.toReminderEntity())

        val reminderDtoAsJson = Gson().toJson(reminder.toReminderDto())
        val uploadReminderRequest = OneTimeWorkRequestBuilder<UploadReminderWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).setInputData(
                workDataOf(
                    "CREATE_REMINDER" to reminderDtoAsJson
                )
            )
            .build()
        WorkManager
            .getInstance(appContext)
            .enqueue(uploadReminderRequest)
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): AuthResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(reminderId: String): AuthResult<AgendaItem.Reminder> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(reminderId: String): AuthResult<Unit> {
        TODO("Not yet implemented")
    }
}
