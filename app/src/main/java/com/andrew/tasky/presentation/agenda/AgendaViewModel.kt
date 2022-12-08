package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.domain.models.AgendaItem
import com.andrew.tasky.domain.models.CalendarDateItem
import com.andrew.tasky.domain.repository.AgendaItemRepository
import com.andrew.tasky.util.UiAgendaItem
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
    val dateSelected = _dateSelected.asStateFlow()
    private var _currentDateAndTime = MutableStateFlow(LocalDateTime.now())
    private val currentDateAndTime = _currentDateAndTime.asStateFlow()

    private val currentDateAndTimeFlow = flow<LocalDateTime> {
        var dateAndTime = LocalDateTime.now()
        while (true) {
            delay(1000L)
            dateAndTime = LocalDateTime.now()
            emit(dateAndTime)
        }
    }

    private fun subscribeToObservables() {
        viewModelScope.launch {
            currentDateAndTimeFlow.collect {
                _currentDateAndTime.value = it
            }
        }
    }

    val agendaItems = repository
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
        var currentDateTimeIndex = 0
        for (item in agendaItems) {
            if (item.startDateAndTime < currentDateTime) {
                currentDateTimeIndex++
            }
        }
        return currentDateTimeIndex
    }

    val uiAgendaItems = agendaItems.map { items ->
        items.map { item ->
            UiAgendaItem.Item(item)
        }
            .toMutableList<UiAgendaItem>()
            .apply {
                add(
                    indexOfTimeNeedle(
                        agendaItems = items,
                        currentDateTime = currentDateAndTime.value
                    ),
                    UiAgendaItem.TimeNeedle
                )
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDateSelected(dateUserSelected: LocalDate) {
        _dateSelected.value = dateUserSelected
        _calendarDateItemList.value = calendarDateItemList.value.map { item ->
            CalendarDateItem(
                isSelected = dateUserSelected == item.date,
                date = item.date
            )
        }
    }

    private val _calendarDateItemList = MutableStateFlow(emptyList<CalendarDateItem>())
    val calendarDateItemList = _calendarDateItemList.asStateFlow()

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        viewModelScope.launch {
            repository.deleteAgendaItem(agendaItem)
        }
    }

    init {

        subscribeToObservables()

        val daysAfterCurrentDate = 5
        val calendarList = (0..daysAfterCurrentDate).mapIndexed { i, days ->
            CalendarDateItem(
                isSelected = i == 0,
                date = LocalDate.now().plusDays(days.toLong())
            )
        }
        _calendarDateItemList.value = calendarList
    }
}
