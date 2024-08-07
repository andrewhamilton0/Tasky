package com.andrew.tasky.agenda.presentation.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.andrew.tasky.agenda.data.agenda.workManagers.SyncAgendaWorker
import com.andrew.tasky.agenda.domain.*
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.CalendarDateItem
import com.andrew.tasky.agenda.util.DateType
import com.andrew.tasky.agenda.util.UiAgendaItem
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.core.domain.SharedPrefs
import com.andrew.tasky.core.domain.StringToInitials
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@HiltViewModel
class AgendaViewModel@Inject constructor(
    private val agendaRepository: AgendaRepository,
    private val reminderRepository: ReminderRepository,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    workManager: WorkManager,
    prefs: SharedPrefs
) : ViewModel() {

    private val fullName = prefs.getFullName()
    val nameInitials = StringToInitials.convertStringToInitials(fullName)

    private val _dateSelected = MutableStateFlow(LocalDate.now())
    val dateSelected = _dateSelected.asStateFlow()
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
        agendaRepository.getAgendaItemsOfDateFlow(date)
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
            withContext(NonCancellable) {
                when (agendaItem) {
                    is AgendaItem.Event -> {
                        eventRepository.toggleIsDone(agendaItem.id)
                    }
                    is AgendaItem.Reminder -> {
                        reminderRepository.toggleIsDone(agendaItem.id)
                    }
                    is AgendaItem.Task -> {
                        taskRepository.toggleIsDone(agendaItem.id)
                    }
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
            withContext(NonCancellable) {
                when (agendaItem) {
                    is AgendaItem.Event -> eventRepository.deleteEvent(agendaItem.id)
                    is AgendaItem.Reminder -> reminderRepository.deleteReminder(agendaItem.id)
                    is AgendaItem.Task -> taskRepository.deleteTask(agendaItem.id)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                authRepository.logout()
            }
        }
    }

    private val syncFullAgendaWorkRequest =
        PeriodicWorkRequestBuilder<SyncAgendaWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            workManager.apply {
                enqueue(syncFullAgendaWorkRequest)
            }
        }
        viewModelScope.launch {
            dateSelected.collectLatest { agendaRepository.updateAgendaItemCache(it) }
        }
    }
}
