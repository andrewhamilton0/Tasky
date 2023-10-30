package com.andrew.tasky.agenda.presentation.screens.event_detail

import android.net.Uri
import androidx.lifecycle.*
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.UriByteConverter
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.util.ReminderTime
import com.andrew.tasky.agenda.util.UiEventPhoto
import com.andrew.tasky.core.UiText
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.core.domain.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: EventRepository,
    private val uriByteConverter: UriByteConverter,
    private val prefs: SharedPrefs
) : ViewModel() {

    private var hostId: String? = null
    private val deletedPhotos = mutableListOf<EventPhoto>()
    private val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")

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

    private val _selectedStartDateTime = MutableStateFlow(LocalDateTime.now())
    val selectedStartDateTime = _selectedStartDateTime.asStateFlow()

    private val _selectedEndDateTime = MutableStateFlow(LocalDateTime.now())
    val selectedEndDateTime = _selectedEndDateTime.asStateFlow()

    private fun setStartDateTime(newDateTime: LocalDateTime) {
        _selectedStartDateTime.update { newDateTime }
        if (selectedEndDateTime.value < selectedStartDateTime.value) {
            _selectedEndDateTime.update { selectedStartDateTime.value.plusMinutes(30) }
        }
    }

    private fun setEndDateTime(newDateTime: LocalDateTime) {
        _selectedEndDateTime.update { newDateTime }
        if (selectedEndDateTime.value < selectedStartDateTime.value) {
            _selectedStartDateTime.update { selectedEndDateTime.value.minusMinutes(30) }
        }
    }

    fun setStartDate(selectedStartDate: LocalDate) {
        val newDateTime = LocalDateTime.of(
            selectedStartDate,
            selectedStartDateTime.value.toLocalTime()
        )
        setStartDateTime(newDateTime)
    }

    fun setStartTime(selectedStartTime: LocalTime) {
        val newDateTime = LocalDateTime.of(
            selectedStartDateTime.value.toLocalDate(),
            selectedStartTime
        )
        setStartDateTime(newDateTime)
    }

    fun setEndDate(selectedEndDate: LocalDate) {
        val newDateTime = LocalDateTime.of(
            selectedEndDate,
            selectedEndDateTime.value.toLocalTime()
        )
        setEndDateTime(newDateTime)
    }

    fun setEndTime(selectedEndTime: LocalTime) {
        val newDateTime = LocalDateTime.of(
            selectedEndDateTime.value.toLocalDate(),
            selectedEndTime
        )
        setEndDateTime(newDateTime)
    }

    private val _selectedReminderTime = MutableStateFlow(ReminderTime.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTime) {
        _selectedReminderTime.value = selectedReminderTime
    }

    private val _photos = MutableStateFlow(listOf<EventPhoto>())
    val photos = _photos.asStateFlow()
    fun addPhoto(uri: Uri) {
        viewModelScope.launch {
            val byteArray = uriByteConverter.uriToByteArray(uri = uri)
            val bitmap = BitmapConverters.byteArrayToBitmap(byteArray)
            val photo = EventPhoto.Local(bitmap = bitmap, byteArray = byteArray)
            _photos.value += photo
        }
    }
    fun deletePhoto(photo: EventPhoto) {
        deletedPhotos.add(photo)
        _photos.value -= photo
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
    private val attendees = _attendees.asStateFlow()

    fun addAttendee(email: String) {
        viewModelScope.launch {
            val result = repository.getAttendee(email)
            when (result) {
                is Resource.Error -> {
                    if (result.message != null) {
                        addAttendeeChannel.send(Resource.Error(result.message))
                    } else {
                        addAttendeeChannel.send(
                            Resource.Error(
                                errorMessage = UiText.Resource(
                                    resId = R.string.unknown_error
                                )
                            )
                        )
                    }
                }
                is Resource.Success -> {
                    if (result.data != null) {
                        val attendee = result.data.copy(isGoing = true)
                        if (!attendees.value.contains(attendee)) {
                            _attendees.value += attendee
                            addAttendeeChannel.send(
                                Resource.Success()
                            )
                        } else {
                            addAttendeeChannel.send(
                                Resource.Error(
                                    errorMessage = UiText.Resource(
                                        resId = R.string.attendee_already_added
                                    )
                                )
                            )
                        }
                    } else {
                        addAttendeeChannel.send(
                            Resource.Error(
                                errorMessage = UiText.Resource(
                                    resId = R.string.unknown_error
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private val addAttendeeChannel = Channel<Resource<Unit>>()
    val addAttendee = addAttendeeChannel.receiveAsFlow()

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
            photos.isNotEmpty() -> true
            else -> false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val _photoOpened = MutableStateFlow<EventPhoto?>(null)
    val photoOpened = _photoOpened.asStateFlow()
    fun setPhotoOpened(eventPhoto: EventPhoto) {
        _photoOpened.value = eventPhoto
    }

    enum class AttendeeFilterTypes {
        ALL,
        GOING,
        NOT_GOING
    }

    fun saveEvent() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                _isSavingEvent.update { true }
                val (_, deletedPhotoCount) = repository.upsertEvent(getEvent())
                if (deletedPhotoCount == 1) {
                    photosNotAddedToastMessageChannel.send(
                        UiText.Resource(
                            resId = R.string.one_photo_not_added
                        )
                    )
                } else if (deletedPhotoCount > 1) {
                    photosNotAddedToastMessageChannel.send(
                        UiText.Resource(
                            resId = R.string.photos_not_added,
                            args = arrayOf(deletedPhotoCount)
                        )
                    )
                }
            }
            finishedSavingEventChannel.send(Unit)
        }
    }

    private fun getEvent(): AgendaItem.Event {
        return AgendaItem.Event(
            id = savedStateHandle.get<String>("id")
                ?: UUID.randomUUID().toString(),
            isDone = isDone.value,
            title = title.value,
            description = description.value,
            startDateAndTime = selectedStartDateTime.value,
            endDateAndTime = selectedEndDateTime.value,
            reminderTime = selectedReminderTime.value,
            photos = photos.value,
            attendees = attendees.value,
            isCreator = isCreator.value,
            host = hostId,
            deletedPhotos = deletedPhotos,
            isGoing = isAttendeeGoing.value
        )
    }

    private val _isSavingEvent = MutableStateFlow(false)
    val isSavingEvent = _isSavingEvent.asStateFlow()

    private val photosNotAddedToastMessageChannel = Channel<UiText>()
    val photosNotAddedToastMessage = photosNotAddedToastMessageChannel.receiveAsFlow()

    private val finishedSavingEventChannel = Channel<Unit>()
    val finishedSavingEvent = finishedSavingEventChannel.receiveAsFlow()

    private val _isAttendeeGoing = MutableStateFlow(true)
    val isAttendeeGoing = _isAttendeeGoing.asStateFlow()

    fun leaveEvent() {
        _isAttendeeGoing.value = false
        _attendees.value = attendees.value.map { attendee ->
            if (prefs.matchesSavedUserId(attendee.userId)) {
                attendee.copy(isGoing = false)
            } else attendee
        }
    }

    fun joinEvent() {
        _isAttendeeGoing.value = true
        _attendees.value = attendees.value.map { attendee ->
            if (prefs.matchesSavedUserId(attendee.userId)) {
                attendee.copy(isGoing = true)
            } else attendee
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                savedStateHandle.get<String>("id")?.let {
                    repository.deleteEvent(it)
                }
            }
        }
    }

    init {
        savedStateHandle.get<String>("id")?.let { id ->
            viewModelScope.launch {
                repository.getEvent(id)?.let { event ->
                    _isCreator.update { event.isCreator }
                    setIsDone(event.isDone)
                    setTitle(event.title)
                    setDescription(event.description)
                    setStartTime(event.startDateAndTime.toLocalTime())
                    setStartDate(event.startDateAndTime.toLocalDate())
                    setEndTime(event.endDateAndTime.toLocalTime())
                    setEndDate(event.endDateAndTime.toLocalDate())
                    setSelectedReminderTime(event.reminderTime)
                    _photos.update { event.photos }
                    _attendees.update { event.attendees }
                    _isAttendeeGoing.update { event.isGoing }
                    hostId = event.host
                    deletedPhotos.addAll(event.deletedPhotos)
                }
            }
        } ?: savedStateHandle.get<String>("initialDate")?.let { dateString ->
            setStartDate(LocalDate.parse(dateString, formatter))
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }
    }
}
