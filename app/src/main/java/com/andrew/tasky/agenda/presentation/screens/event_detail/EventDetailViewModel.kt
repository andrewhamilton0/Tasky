package com.andrew.tasky.agenda.presentation.screens.event_detail

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.util.ReminderTime
import com.andrew.tasky.agenda.util.UiEventPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: EventRepository,
    private val prefs: SharedPreferences
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

    private val _isCreator = MutableStateFlow(true)
    val isCreator = _isCreator.asStateFlow()

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

    private val _photo = MutableStateFlow(listOf<EventPhoto>())
    val photos = _photo.asStateFlow()
    fun addPhoto(photo: EventPhoto) {
        _photo.value += photo
    }
    fun deletePhoto(indexToDelete: Int) {
        val updatedPhotos = photos.value.filterIndexed { currentIndex, _ ->
            currentIndex != indexToDelete
        }
        _photo.value = updatedPhotos
    }

    val uiEventPhotos = photos.combine(isCreator) { photos, isCreator ->
        photos.map { photo ->
            UiEventPhoto.Photo(photo)
        }.toMutableList<UiEventPhoto>()
            .apply {
                if ((size in 1..9) && (isCreator)) {
                    add(UiEventPhoto.AddPhoto)
                }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _attendees = MutableStateFlow(listOf<Attendee>())
    val attendees = _attendees.asStateFlow()
    fun addAttendee(attendee: Attendee) {
        _attendees.value += attendee
    }

    val goingAttendees = attendees.map {
        it.filter { attendee -> attendee.isGoing }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notGoingAttendees = attendees.map {
        it.filter { attendee -> !attendee.isGoing }
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

    val isCreatorEditing = combine(isCreator, isInEditMode) { isCreator, isEditing ->
        isCreator && isEditing
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val allowedToSeePhotoLayout = combine(isCreator, uiEventPhotos) { isCreator, photos ->
        when {
            isCreator -> true
            !isCreator && photos.isNotEmpty() -> true
            else -> false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    enum class AttendeeFilterTypes {
        ALL,
        GOING,
        NOT_GOING
    }

    fun saveAgendaItem() {
        val event = AgendaItem.Event(
            id = savedStateHandle.get<AgendaItem.Event>("event")?.id
                ?: UUID.randomUUID().toString(),
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
            attendees = attendees.value,
            isCreator = false, // TODO make isCreator
            host = "BLANK", // TODO make host
            deletedPhotoKeys = emptyList(), // TODO setup deletedPhotosKeys
            isGoing = true // TODO setup is going
        )
        // viewModelScope gets cancelled as soon as the Fragment is popped from the backstack,
        // so if you pop it right after inserting an element, this coroutine will be cancelled
        // before it can finish inserting the element. With NonCancellable we make sure it's not
        // going to be cancelled.
        viewModelScope.launch {
            withContext(NonCancellable) {
                repository.createEvent(event)
            }
        }
    }

    fun leaveEvent() {
    }

    fun deleteAgendaItem() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                savedStateHandle.get<AgendaItem.Event>("agendaItem")?.let {
                    // repository.deleteAgendaItem(it)
                }
            }
        }
    }

    init {
        savedStateHandle.get<AgendaItem.Event>("event")?.let { item ->
            _isCreator.value = item.isCreator
            setIsDone(item.isDone)
            setTitle(item.title)
            setDescription(item.description)
            setStartTime(item.startDateAndTime.toLocalTime())
            setStartDate(item.startDateAndTime.toLocalDate())
            setEndTime(item.endDateAndTime.toLocalTime())
            setEndDate(item.endDateAndTime.toLocalDate())
            setSelectedReminderTime(item.reminderTime)
            _photo.value = item.photos
            _attendees.value = item.attendees
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }
    }
}
