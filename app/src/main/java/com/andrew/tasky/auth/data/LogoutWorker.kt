package com.andrew.tasky.auth.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.auth.util.getAuthResult
import com.andrew.tasky.core.WorkerParamKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogoutWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val authApi: AuthApi
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.e("Logout work request", "running")
        val token = workerParams.inputData.getString(WorkerParamKeys.TOKEN)
        val result = getAuthResult { authApi.logout("Bearer $token") }
        return when (result) {
            is AuthResult.Authorized -> {
                Result.success()
            }
            is AuthResult.Unauthorized -> {
                Log.e("Logout work request", "unauthorized")
                Result.failure()
            }
            is AuthResult.UnknownError -> {
                Log.e("Logout work request", "unknown error")
                Result.retry()
            }
        }
    }
}
