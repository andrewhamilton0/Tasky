package com.andrew.tasky.agenda.data.agenda

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.domain.AgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncModifiedAgendaItemsWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val agendaRepository: AgendaRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        agendaRepository.syncAgendaItems()
        return Result.success()
    }
}
