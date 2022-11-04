package com.andrew.tasky.presentation.agenda

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgendaViewModel : ViewModel() {

    private val _dateSelected = MutableStateFlow(LocalDate.now())
    val dateSelected = _dateSelected.asStateFlow()
    fun setDateSelected(dateUserSelected: LocalDate) {
        _dateSelected.value = dateUserSelected
    }
}
