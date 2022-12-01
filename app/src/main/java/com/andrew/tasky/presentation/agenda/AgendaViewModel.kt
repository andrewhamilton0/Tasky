package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.domain.models.AgendaItem
import com.andrew.tasky.domain.models.CalendarDateItem
import com.andrew.tasky.domain.repository.AgendaItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class AgendaViewModel@Inject constructor(
    private val repository: AgendaItemRepository
) : ViewModel() {

    private val _dateSelected = MutableStateFlow(LocalDate.now())
    val dateSelected = _dateSelected.asStateFlow()

    // The combine block will automatically be called every time either agendaItems or the selected
    // date is changed and therefore properly recalculate the list as needed.
    val agendaItems = repository
        .getAgendaItems()
        .combine(dateSelected) { items, selectedDate ->
            items
                .filter { it.startDateAndTime.toLocalDate() == selectedDate }
                .sortedBy { it.startDateAndTime }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

        val daysAfterCurrentDate = 5
        var calendarList = listOf<CalendarDateItem>(
            CalendarDateItem(
                isSelected = true,
                date = LocalDate.now()
            )
        )
        calendarList += (1..daysAfterCurrentDate).map { days ->
            CalendarDateItem(
                isSelected = false,
                date = LocalDate.now().plusDays(days.toLong())
            )
        }
        calendarList = calendarList.sortedBy {
            it.date
        }
        _calendarDateItemList.value = calendarList
    }
}
