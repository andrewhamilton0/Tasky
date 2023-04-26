package com.andrew.tasky.agenda.data.agenda.workManagers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.domain.AgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncAgendaWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val agendaRepository: AgendaRepository
) : CoroutineWorker(appContext = appContext, params = workerParams) {

    override suspend fun doWork(): Result {
        agendaRepository.syncFullAgenda()
        return Result.success()
    }
}
