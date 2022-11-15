package com.andrew.tasky.presentation.agenda_item_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.Photo
import com.andrew.tasky.util.ReminderTime
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.*

class AgendaItemDetailViewModel : ViewModel() {

    private val _isInitiallySetup = MutableStateFlow(false)
    val isInitiallySetup = _isInitiallySetup.asStateFlow()
    fun setInitialSetupToTrue() {
        _isInitiallySetup.value = true
    }

    private val _isInEditMode = MutableStateFlow(false)
    val isInEditMode = _isInEditMode.asStateFlow()
    fun setEditMode(isEditing: Boolean) {
        _isInEditMode.value = isEditing
    }

    private val _isDone = MutableStateFlow(false)
    val isDone = _isDone.asStateFlow()
    fun setIsDone(isDone: Boolean) {
        _isDone.value = isDone
    }

    private val _title = MutableStateFlow("Blank Title")
    val title = _title.asStateFlow()
    fun setTitle(title: String) {
        _title.value = title
    }

    private val _description = MutableStateFlow("Blank Description")
    val description = _description.asStateFlow()
    fun setDescription(description: String) {
        _description.value = description
    }

    private val _selectedStartDate = MutableStateFlow(LocalDate.now())
    val selectedStartDate = _selectedStartDate.asStateFlow()
    fun setStartDate(selectedStartDate: LocalDate) {
        _selectedStartDate.value = selectedStartDate
    }

    private val _selectedStartTime = MutableStateFlow(LocalTime.now())
    val selectedStartTime = _selectedStartTime.asStateFlow()
    fun setStartTime(selectedStartTime: LocalTime) {
        _selectedStartTime.value = selectedStartTime
    }

    private val _selectedEndDate = MutableStateFlow(LocalDate.now())
    val selectedEndDate = _selectedEndDate.asStateFlow()
    fun setEndDate(selectedEndDate: LocalDate) {
        _selectedStartDate.value = selectedEndDate
    }

    private val _selectedEndTime = MutableStateFlow(LocalTime.now())
    val selectedEndTime = _selectedEndTime.asStateFlow()
    fun setEndTime(selectedEndTime: LocalTime) {
        _selectedEndTime.value = selectedEndTime
    }

    private val _selectedReminderTime = MutableStateFlow(ReminderTime.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTime) {
        _selectedReminderTime.value = selectedReminderTime
    }

    private val _photos = MutableStateFlow(listOf<Photo>())
    val photos = _photos.asStateFlow()
    fun addPhoto(photo: Photo) {
        _photos.value += photo
    }
    fun deletePhoto(index1: Int) {
        val updatedPhotos = photos.value.filterIndexed { index, _ -> index != index1 }
        _photos.value = updatedPhotos
    }
    fun setupPhotos(photoList: List<Photo>) {
        _photos.value = photoList
    }

    private val _attendees = MutableStateFlow(listOf<Attendee>())
    val attendees = _attendees.asStateFlow()
    fun addAttendee(attendee: Attendee) {
        _attendees.value += attendee
    }

    val goingAttendees = attendees.map {
        it.filter { attendee -> attendee.isAttending }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notGoingAttendees = attendees.map {
        it.filter { attendee -> !attendee.isAttending }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteAttendee(attendee: Attendee) {
        val updatedAttendees = attendees.value.filter { it != attendee }
        _attendees.value = updatedAttendees
    }

    private val _selectedAttendeeButton = MutableStateFlow(AttendeeButtonTypes.ALL)
    val selectedAttendeeButton = _selectedAttendeeButton.asStateFlow()
    fun showAllAttendees() {
        _selectedAttendeeButton.value = AttendeeButtonTypes.ALL
    }
    fun showGoingAttendees() {
        _selectedAttendeeButton.value = AttendeeButtonTypes.GOING
    }
    fun showNotGoingAttendees() {
        _selectedAttendeeButton.value = AttendeeButtonTypes.NOT_GOING
    }

    private val _isAttending = MutableStateFlow(true)
    val isAttending = _isAttending.asStateFlow()
    fun switchAttendingStatus() {
        _isAttending.value = !isAttending.value
    }

    enum class AttendeeButtonTypes {
        ALL,
        GOING,
        NOT_GOING
    }
}
