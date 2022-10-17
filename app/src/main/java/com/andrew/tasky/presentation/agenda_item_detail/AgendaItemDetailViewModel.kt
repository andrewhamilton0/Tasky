package com.andrew.tasky.presentation.agenda_item_detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.andrew.tasky.util.ReminderTimes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

class AgendaItemDetailViewModel : ViewModel() {

    private val _isInitiallySetup = MutableStateFlow(false)
    val isInitiallySetup = _isInitiallySetup.asStateFlow()
    fun setInitialSetupToTrue(){
        _isInitiallySetup.value = true
    }

    private val _isInEditMode = MutableStateFlow(false)
    val isInEditMode = _isInEditMode.asStateFlow()
    fun setEditMode(isEditing: Boolean){
        _isInEditMode.value = isEditing
    }

    private val _isDone = MutableStateFlow(false)
    val isDone = _isDone.asStateFlow()
    fun setIsDone(isDone: Boolean){
        _isDone.value = isDone
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

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()
    fun setSelectedDate(selectedDate: LocalDate){
        _selectedDate.value = selectedDate
    }

    private val _selectedTime = MutableStateFlow(LocalTime.now())
    val selectedTime = _selectedTime.asStateFlow()
    fun setSelectedTime(selectedTime: LocalTime){
        _selectedTime.value = selectedTime
    }

    private val _selectedReminderTime = MutableStateFlow(ReminderTimes.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTimes){
        _selectedReminderTime.value = selectedReminderTime
    }

    private val _photos = MutableStateFlow(listOf<Uri>())
    val photos = _photos.asStateFlow()
    fun addPhoto(uri: Uri){
        val updatePhotos: List<Uri> = photos.value.toMutableList().apply {
            add(0, uri)
        }
        _photos.value = updatePhotos
    }
    fun deletePhoto(index: Int){
        val updatePhotos: List<Uri> = photos.value.toMutableList().apply {
            removeAt(index)
        }
        _photos.value = updatePhotos
    }
    fun setupPhotos(photoList: List<Uri>){
        _photos.value = photoList
    }
}