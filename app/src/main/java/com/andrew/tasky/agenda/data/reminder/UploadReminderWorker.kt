package com.andrew.tasky.agenda.data.reminder

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val api: ReminderApi,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return super.getForegroundInfo()
    }

    override suspend fun doWork(): Result {

        val reminderEntity = Gson().fromJson(
            workerParams.inputData.getString("CREATE_REMINDER"), ReminderEntity::class.java
        )

        return when (getAuthResult { api.createReminder(reminderEntity) }) {
            is AuthResult.Authorized -> {
                Log.e("REMINDER AUTH Result", "SUCCESS")
                Result.success()
            }
            is AuthResult.Unauthorized -> {
                Log.e("REMINDER AUTH Result", "Unauthorized")
                Result.failure()
            }
            is AuthResult.UnknownError -> {
                Log.e("REMINDER AUTH Result", "Unknown Error")
                Result.failure()
            }
        }
    }
}
