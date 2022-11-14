package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.repository.AgendaItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AgendaViewModel@Inject constructor(
    private val repository: AgendaItemRepository
) : ViewModel() {

    private val _dateSelected = MutableStateFlow(LocalDate.now())
    val dateSelected = _dateSelected.asStateFlow()
    fun setDateSelected(dateUserSelected: LocalDate) {
        _dateSelected.value = dateUserSelected
    }

    fun sortByDateSelected(dateSelected: LocalDate): List<AgendaItem> {
        return repository.getAgendaItems().value?.sortedBy { agendaItem ->
            agendaItem.startDateAndTime
        }?.filter {
            it.startDateAndTime.toLocalDate() == dateSelected
        } ?: emptyList<AgendaItem>()
    }

    fun deleteAgendaItem(agendaItem: AgendaItem) {
        viewModelScope.launch {
            repository.deleteAgendaItem(agendaItem)
        }
    }
}
