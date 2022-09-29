package com.andrew.tasky.presentation.agenda_item_detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaItemDetailViewModel : ViewModel() {

    private val _isInEditMode = MutableStateFlow(false)
    val isInEditMode = _isInEditMode.asStateFlow()
    fun setEditMode(x: Boolean){
        _isInEditMode.value = x
    }

    private val _title = MutableStateFlow("Blank Title")
    val title = _title.asStateFlow()
    fun setTitle(x: String){
        _title.value = x
    }

    private val _description = MutableStateFlow("Blank Description")
    val description = _description.asStateFlow()
    fun setDescription(x: String) {
        _description.value = x
    }

    private val _selectedDate = MutableStateFlow(LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("MMM dd yyyy")))
    val selectedDate = _selectedDate.asStateFlow()
    fun setSelectedDate(x: String?){
        _selectedDate.value = x
    }

    private val _selectedTime = MutableStateFlow(LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("hh:mm a")))
    val selectedTime = _selectedTime.asStateFlow()
    fun setSelectedTime(x: String?){
        _selectedTime.value = x
    }
}