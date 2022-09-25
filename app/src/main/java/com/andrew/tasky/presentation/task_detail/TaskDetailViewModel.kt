package com.andrew.tasky.presentation.task_detail

import androidx.lifecycle.ViewModel
import com.andrew.tasky.util.TaskType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailViewModel : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val _selectedDate = MutableStateFlow(SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format((calendar.time)))
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedTime = MutableStateFlow(SimpleDateFormat("hh:mm a", Locale.ENGLISH).format((calendar.time)))
    val selectedTime = _selectedTime.asStateFlow()

    private var editMode: Boolean = false

    fun setEditMode(x: Boolean){
        editMode = x
    }

    fun getEditMode(): Boolean = editMode

    fun setSelectedDate(x: String?){
        _selectedDate.value = x
    }

    fun setSelectedTime(x: String?){
        _selectedTime.value = x
    }



}