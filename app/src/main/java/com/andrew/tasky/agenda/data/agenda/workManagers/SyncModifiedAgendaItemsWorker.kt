package com.andrew.tasky.agenda.data.agenda.workManagers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.core.data.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncModifiedAgendaItemsWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val agendaRepository: AgendaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = agendaRepository.syncModifiedAgendaItems()
        return when (result) {
            is Resource.Error -> {
                Log.e(
                    "Sync Agenda work request",
                    result.message?.asString(appContext) ?: "unknown error"
                )
                Result.failure()
            }
            is Resource.Success -> {
                Result.success()
            }
        }
    }
}
