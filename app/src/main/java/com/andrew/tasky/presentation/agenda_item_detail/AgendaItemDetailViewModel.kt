package com.andrew.tasky.presentation.agenda_item_detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.andrew.tasky.util.ReminderTimes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _selectedStartDate = MutableStateFlow(LocalDate.now())
    val selectedStartDate = _selectedStartDate.asStateFlow()
    fun setStartDate(selectedStartDate: LocalDate){
        _selectedStartDate.value = selectedStartDate
    }

    private val _selectedStartTime = MutableStateFlow(LocalTime.now())
    val selectedStartTime = _selectedStartTime.asStateFlow()
    fun setStartTime(selectedStartTime: LocalTime){
        _selectedStartTime.value = selectedStartTime
    }

    private val _selectedEndDate = MutableStateFlow(LocalDate.now())
    val selectedEndDate = _selectedEndDate.asStateFlow()
    fun setEndDate(selectedEndDate: LocalDate){
        _selectedStartDate.value = selectedEndDate
    }

    private val _selectedEndTime = MutableStateFlow(LocalTime.now())
    val selectedEndTime = _selectedEndTime.asStateFlow()
    fun setEndTime(selectedEndTime: LocalTime){
        _selectedEndTime.value = selectedEndTime
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

    private val _goingAttendees = MutableStateFlow(listOf<String>())
    val goingAttendees = _goingAttendees.asStateFlow()
    fun addGoingAttendee(goingAttendee: String){
        val updateGoingAttendees: List<String> = goingAttendees.value.toMutableList().apply {
            add(goingAttendee)
        }
        _goingAttendees.value = updateGoingAttendees
    }
    fun removeGoingAttendee(goingAttendee: String){
        val updateGoingAttendees: List<String> = goingAttendees.value.toMutableList().apply {
            remove(goingAttendee)
        }
        _goingAttendees.value = updateGoingAttendees
    }

    private val _notGoingAttendees = MutableStateFlow(listOf<String>())
    val notGoingAttendees = _notGoingAttendees.asStateFlow()
    fun addNotGoingAttendee(notGoingAttendee: String){
        val updateNotGoingAttendees: List<String> = notGoingAttendees.value.toMutableList().apply {
            add(notGoingAttendee)
        }
        _notGoingAttendees.value = updateNotGoingAttendees
    }
    fun removeNotGoingAttendee(notGoingAttendee: String){
        val updateNotGoingAttendees: List<String> = notGoingAttendees.value.toMutableList().apply {
            remove(notGoingAttendee)
        }
        _notGoingAttendees.value = updateNotGoingAttendees
    }

    private val _selectedAttendeeButton = MutableStateFlow(AttendeeButtonTypes.ALL)
    val selectedAttendeeButton = _selectedAttendeeButton.asStateFlow()
    fun showAllAttendees(){
        _selectedAttendeeButton.value = AttendeeButtonTypes.ALL
    }
    fun showGoingAttendees(){
        _selectedAttendeeButton.value = AttendeeButtonTypes.GOING
    }
    fun showNotGoingAttendees(){
        _selectedAttendeeButton.value = AttendeeButtonTypes.NOT_GOING
    }

    private val _isAttending = MutableStateFlow(true)
    val isAttending = _isAttending.asStateFlow()
    fun switchAttendingStatus(){
        _isAttending.value = !isAttending.value
    }

    enum class AttendeeButtonTypes{
        ALL,
        GOING,
        NOT_GOING
    }
}