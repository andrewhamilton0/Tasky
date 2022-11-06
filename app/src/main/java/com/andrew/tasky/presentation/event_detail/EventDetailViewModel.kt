package com.andrew.tasky.presentation.event_detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.util.ReminderTimes
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.flow.*

class EventDetailViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

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
        _selectedEndDate.value = selectedEndDate
    }

    private val _selectedEndTime = MutableStateFlow(LocalTime.now())
    val selectedEndTime = _selectedEndTime.asStateFlow()
    fun setEndTime(selectedEndTime: LocalTime) {
        _selectedEndTime.value = selectedEndTime
    }

    private val _selectedReminderTime = MutableStateFlow(ReminderTimes.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTimes) {
        _selectedReminderTime.value = selectedReminderTime
    }

    private val _photos = MutableStateFlow(listOf<Uri>())
    val photos = _photos.asStateFlow()
    fun addPhoto(uri: Uri) {
        _photos.value += uri
    }
    fun deletePhoto(indexToDelete: Int) {
        val updatedPhotos = photos.value.filterIndexed { currentIndex, _ ->
            currentIndex != indexToDelete
        }
        _photos.value = updatedPhotos
    }
    private fun setupPhotos(photoList: List<Uri>) {
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

    private val _selectedAttendeeFilterType = MutableStateFlow(AttendeeFilterTypes.ALL)
    val selectedAttendeeFilterType = _selectedAttendeeFilterType.asStateFlow()
    fun setAttendeeFilterType(type: AttendeeFilterTypes) {
        _selectedAttendeeFilterType.value = type
    }

    private val _isAttending = MutableStateFlow(true)
    val isAttending = _isAttending.asStateFlow()
    fun switchAttendingStatus() {
        _isAttending.value = !isAttending.value
    }

    enum class AttendeeFilterTypes {
        ALL,
        GOING,
        NOT_GOING
    }

    init {
        savedStateHandle.get<AgendaItem>("agendaItem")?.let { item ->
            setIsDone(item.isDone)
            setTitle(item.title)
            setDescription(item.description)
            setStartTime(item.startDateAndTime.toLocalTime())
            setStartDate(item.startDateAndTime.toLocalDate())
            item.endDateAndTime?.let {
                setEndTime(it.toLocalTime())
                setEndDate(it.toLocalDate())
            }
            setSelectedReminderTime(item.reminderTime)
            item.photos?.let { setupPhotos(it) }
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }
    }
}
