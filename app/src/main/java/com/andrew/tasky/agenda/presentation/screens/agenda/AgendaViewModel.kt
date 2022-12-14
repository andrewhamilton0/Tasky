package com.andrew.tasky.agenda.presentation.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.CalendarDateItem
import com.andrew.tasky.agenda.domain.repository.AgendaItemRepository
import com.andrew.tasky.agenda.util.DateType
import com.andrew.tasky.agenda.util.UiAgendaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AgendaViewModel@Inject constructor(
    private val repository: AgendaItemRepository
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

    private val agendaItems = repository
        .getAgendaItems()
        .combine(dateSelected) { items, selectedDate ->
            items
                .filter { it.startDateAndTime.toLocalDate() == selectedDate }
                .sortedBy { it.startDateAndTime }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            val updatedAgendaItem = agendaItem.copy(isDone = !agendaItem.isDone)
            repository.upsert(updatedAgendaItem)
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
            repository.deleteAgendaItem(agendaItem)
        }
    }
}
