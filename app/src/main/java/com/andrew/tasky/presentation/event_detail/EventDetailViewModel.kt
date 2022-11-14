package com.andrew.tasky.presentation.event_detail

import android.net.Uri
import androidx.lifecycle.*
import com.andrew.tasky.domain.AgendaItem
import com.andrew.tasky.domain.Attendee
import com.andrew.tasky.domain.repository.AgendaItemRepository
import com.andrew.tasky.util.AgendaItemType
import com.andrew.tasky.util.ReminderTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AgendaItemRepository
) : ViewModel() {

    private var id: Int? = null
    private val agendaItemType = AgendaItemType.EVENT

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

    private val _selectedReminderTime = MutableStateFlow(ReminderTime.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTime) {
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
    private fun setupAttendeeList(attendeeList: List<Attendee>) {
        _attendees.value = attendeeList
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

    fun saveAgendaItem() {
        val agendaItem = AgendaItem(
            id = id,
            type = agendaItemType,
            isDone = isDone.value,
            title = title.value,
            description = description.value,
            startDateAndTime = LocalDateTime.of(
                selectedStartDate.value,
                selectedStartTime.value
            ),
            endDateAndTime = LocalDateTime.of(
                selectedEndDate.value,
                selectedEndTime.value
            ),
            reminderTime = selectedReminderTime.value,
            photos = photos.value,
            attendees = attendees.value
        )
        // viewModelScope gets cancelled as soon as the Fragment is popped from the backstack,
        // so if you pop it right after inserting an element, this coroutine will be cancelled
        // before it can finish inserting the element. With NonCancellable we make sure it's not
        // going to be cancelled.
        viewModelScope.launch {
            withContext(NonCancellable) {
                repository.upsert(agendaItem)
            }
        }
    }

    fun deleteAgendaItem() {
        viewModelScope.launch {
            savedStateHandle.get<AgendaItem>("agendaItem")?.let { repository.deleteAgendaItem(it) }
        }
    }

    init {
        savedStateHandle.get<AgendaItem>("agendaItem")?.let { item ->
            id = item.id
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
            item.attendees?.let { setupAttendeeList(it) }
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }
    }
}
