package com.andrew.tasky.presentation.agenda_item_detail

import androidx.lifecycle.ViewModel
import com.andrew.tasky.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AgendaItemDetailViewModel : ViewModel() {

    private val _isInEditMode = MutableStateFlow(false)
    val isInEditMode = _isInEditMode.asStateFlow()
    fun setEditMode(isEditing: Boolean){
        _isInEditMode.value = isEditing
    }

    private val _title = MutableStateFlow("Blank Title")
    val title = _title.asStateFlow()
    fun setTitle(title: String){
        _title.value = title
    }

    private val _description = MutableStateFlow("Blank Description")
    val description = _description.asStateFlow()
    fun setDescription(description: String) {
        _description.value = description
    }

    private val _selectedDate = MutableStateFlow(LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("MMM dd yyyy")))
    val selectedDate = _selectedDate.asStateFlow()
    fun setSelectedDate(selectedDate: String?){
        _selectedDate.value = selectedDate
    }

    private val _selectedTime = MutableStateFlow(LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("hh:mm a")))
    val selectedTime = _selectedTime.asStateFlow()
    fun setSelectedTime(selectedTime: String?){
        _selectedTime.value = selectedTime
    }

    private val _selectedReminderTime = MutableStateFlow("10 minutes before")
    val selectedReminderTime= _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: String){
        _selectedReminderTime.value = selectedReminderTime
    }
}