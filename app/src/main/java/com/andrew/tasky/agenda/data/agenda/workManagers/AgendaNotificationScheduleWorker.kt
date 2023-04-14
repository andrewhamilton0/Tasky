package com.andrew.tasky.agenda.data.agenda.workManagers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andrew.tasky.agenda.data.event.toNotificationInfo
import com.andrew.tasky.agenda.data.reminder.toNotificationInfo
import com.andrew.tasky.agenda.data.task.toNotificationInfo
import com.andrew.tasky.agenda.domain.AgendaNotificationScheduler
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.ZonedDateTime

@HiltWorker
class AgendaNotificationScheduleWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    private val agendaRepository: AgendaRepository,
    private val scheduler: AgendaNotificationScheduler

) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val items = agendaRepository.getOneTimeAgendaItemsBetweenTimes(
            startEpochMilli = ZonedDateTime.now().toInstant().toEpochMilli(),
            endEpochMilli = ZonedDateTime.now().plusWeeks(1).toInstant().toEpochMilli()
        )
        items.forEach { item ->
            when (item) {
                is AgendaItem.Event -> scheduler.schedule(item.toNotificationInfo())
                is AgendaItem.Reminder -> scheduler.schedule(item.toNotificationInfo())
                is AgendaItem.Task -> scheduler.schedule(item.toNotificationInfo())
            }
        }
        return Result.success()
    }
}
