package com.andrew.tasky.agenda.data.agenda

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.core.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncModifiedAgendaItemsWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: AgendaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = agendaRepository.syncModifiedAgendaItems()
        return when (result) {
            is Resource.Error -> {
                Result.success()
            }
            is Resource.Success -> {
                Log.e("Sync Agenda work request", result.message ?: "unknown error")
                Result.failure()
            }
        }
    }
}
