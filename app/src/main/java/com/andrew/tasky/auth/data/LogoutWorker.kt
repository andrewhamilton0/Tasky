package com.andrew.tasky.auth.data

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.core.util.WorkerParamKeys
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogoutWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val authApi: AuthApi
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val token = workerParams.inputData.getString(WorkerParamKeys.TOKEN)
        val result = getResourceResult { authApi.logout("Bearer $token") }
        return when (result) {
            is Resource.Error -> {
                Log.e(
                    "Logout work request",
                    result.message?.asString(appContext) ?: "unknown error"
                )
                Result.retry()
            }
            is Resource.Success -> Result.success()
        }
    }
}
