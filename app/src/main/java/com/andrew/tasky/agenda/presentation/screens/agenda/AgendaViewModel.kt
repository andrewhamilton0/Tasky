package com.andrew.tasky.agenda.presentation.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.andrew.tasky.agenda.data.agenda.SyncModifiedAgendaItemsWorker
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.agenda.domain.ReminderRepository
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.CalendarDateItem
import com.andrew.tasky.agenda.util.DateType
import com.andrew.tasky.agenda.util.UiAgendaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AgendaViewModel@Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _dateSelected = MutableStateFlow(LocalDate.now())
    private val dateSelected = _dateSelected.asStateFlow()

    fun setDateSelected(dateUserSelected: LocalDate) {
        _dateSelected.value = dateUserSelected
    }

    val currentDateAndTimeFlow = flow<LocalDateTime> {
        var dateAndTime = LocalDateTime.now()
        while (true) {
            emit(dateAndTime)
            delay(1000L)
            dateAndTime = LocalDateTime.now()
        }
    }
    private val agendaItems = dateSelected.flatMapLatest { date ->
        agendaRepository.getAgendaItems(date).flattenMerge()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun indexOfTimeNeedle(
        agendaItems: List<AgendaItem>,
        currentDateTime: LocalDateTime
    ): Int {
        return agendaItems.indexOf(
            agendaItems.findLast { it.startDateAndTime < currentDateTime } ?: return 0
        ).plus(1)
    }

    val uiAgendaItems = combine(agendaItems, currentDateAndTimeFlow) { items, currentTime ->
        items.map { item ->
            UiAgendaItem.Item(item)
        }
            .toMutableList<UiAgendaItem>()
            .apply {
                add(
                    indexOfTimeNeedle(
                        agendaItems = items,
                        currentDateTime = currentTime
                    ),
                    UiAgendaItem.TimeNeedle
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchIsDone(agendaItem: AgendaItem) {
        viewModelScope.launch {
            when (agendaItem) {
                is AgendaItem.Event -> TODO()
                is AgendaItem.Reminder -> {
                    reminderRepository.updateReminder(agendaItem.copy(isDone = !agendaItem.isDone))
                }
                is AgendaItem.Task -> {
                    taskRepository.updateTask(agendaItem.copy(isDone = !agendaItem.isDone))
                }
            }
        }
    }

    private val daysAfterCurrentDate = 5
    val calendarDateItemList = combine(
        dateSelected, currentDateAndTimeFlow
    ) { dateSelected, currentDateTime ->
        (0..daysAfterCurrentDate).map { days ->
            CalendarDateItem(
                isSelected = dateSelected == currentDateTime.toLocalDate().plusDays(days.toLong()),
                date = currentDateTime.toLocalDate().plusDays(days.toLong())
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentDateType = combine(
        dateSelected, currentDateAndTimeFlow
    ) { dateSelected, currentDateTime ->
        val currentDate = currentDateTime.toLocalDate()
        when (dateSelected) {
            currentDate.minusDays(1) -> {
                DateType.Yesterday
            }
            currentDate -> {
                DateType.Today
            }
            currentDate.plusDays(1) -> {
                DateType.Tomorrow
            }
            else -> {
                DateType.FullDate(dateSelected)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DateType.Today)

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        viewModelScope.launch {
            when (agendaItem) {
                is AgendaItem.Event -> TODO()
                is AgendaItem.Reminder -> reminderRepository.deleteReminder(agendaItem)
                is AgendaItem.Task -> taskRepository.deleteTask(agendaItem)
            }
        }
    }

    private val syncModifiedAgendaItemsWorkRequest =
        PeriodicWorkRequestBuilder<SyncModifiedAgendaItemsWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

    init {
        workManager.enqueue(syncModifiedAgendaItemsWorkRequest)
    }
}
