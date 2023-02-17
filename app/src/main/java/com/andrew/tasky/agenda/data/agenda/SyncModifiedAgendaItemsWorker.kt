package com.andrew.tasky.agenda.data.agenda

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.util.getAuthResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncModifiedAgendaItemsWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: AgendaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = getAuthResult { agendaRepository.syncModifiedAgendaItems() }
        return when (result) {
            is AuthResult.Authorized -> {
                Result.success()
            }
            is AuthResult.Unauthorized -> {
                Log.e("Sync Agenda work request", "unauthorized")
                Result.failure()
            }
            is AuthResult.UnknownError -> {
                Log.e("Sync Agenda work request", "unknown error")
                Result.failure()
            }
        }
    }
}
