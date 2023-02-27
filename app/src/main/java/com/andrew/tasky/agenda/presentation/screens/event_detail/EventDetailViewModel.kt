package com.andrew.tasky.agenda.presentation.screens.event_detail

import android.net.Uri
import androidx.lifecycle.*
import com.andrew.tasky.R
import com.andrew.tasky.agenda.data.event.photo.LocalPhotoDto
import com.andrew.tasky.agenda.data.util.BitmapConverters
import com.andrew.tasky.agenda.domain.EventRepository
import com.andrew.tasky.agenda.domain.UriByteConverter
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.models.Attendee
import com.andrew.tasky.agenda.domain.models.EventPhoto
import com.andrew.tasky.agenda.util.ReminderTime
import com.andrew.tasky.agenda.util.UiEventPhoto
import com.andrew.tasky.core.Resource
import com.andrew.tasky.core.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    private val uriByteConverter: UriByteConverter
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

    private val _photo = MutableStateFlow(listOf<UiEventPhoto>())
    private val photos = _photo.asStateFlow()
    fun addPhoto(uri: Uri) {
        viewModelScope.launch {
            val byteArray = uriByteConverter.uriToByteArray(uri = uri)
            val photo = UiEventPhoto.LocalPhoto(
                bitmap = BitmapConverters.byteArrayToBitmap(byteArray)
            )
            _photo.value += photo
        }
    }
    fun deletePhoto(indexToDelete: Int) {
        val updatedPhotos = photos.value.filterIndexed { currentIndex, _ ->
            currentIndex != indexToDelete
        }
        _photo.value = updatedPhotos
    }

    val uiEventPhotos = photos.combine(isCreator) { photos, isCreator ->
        photos.map { photo ->
            photo
        }.toMutableList()
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
                        attendeeToastMessageChannel.send(result.message)
                    } else {
                        attendeeToastMessageChannel.send(
                            UiText.Resource(
                                resId = R.string.unknown_error
                            )
                        )
                    }
                }
                is Resource.Success -> {
                    if (result.data != null) {
                        val attendee = result.data.copy(isGoing = true)
                        if (!attendees.value.contains(attendee)) {
                            _attendees.value += attendee
                        } else {
                            attendeeToastMessageChannel.send(
                                UiText.Resource(
                                    resId = R.string.attendee_already_added
                                )
                            )
                        }
                    } else {
                        attendeeToastMessageChannel.send(
                            UiText.Resource(
                                resId = R.string.unknown_error
                            )
                        )
                    }
                }
            }
        }
    }

    private val attendeeToastMessageChannel = Channel<UiText>()
    val attendeeToastMessage = attendeeToastMessageChannel.receiveAsFlow()

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

    fun saveEvent() {

        // viewModelScope gets cancelled as soon as the Fragment is popped from the backstack,
        // so if you pop it right after inserting an element, this coroutine will be cancelled
        // before it can finish inserting the element. With NonCancellable we make sure it's not
        // going to be cancelled.
        viewModelScope.launch {
            withContext(NonCancellable) {
                var photosDeleted = 0
                val targetSize = 1000000
                val eventPhotos = uiEventPhotos.value
                    .mapNotNull { eventPhoto ->
                        when (eventPhoto) {
                            is UiEventPhoto.LocalPhoto -> {
                                if (eventPhoto.key == null) {
                                    val compressedByteArray = eventPhoto.bitmap.let { bitmap ->
                                        BitmapConverters.bitmapToCompressByteArray(
                                            bitmap = bitmap,
                                            targetSize = targetSize
                                        )
                                    }
                                    val key = UUID.randomUUID().toString()
                                    compressedByteArray?.let {
                                        LocalPhotoDto(
                                            key = key,
                                            byteArray = it
                                        )
                                    }?.let { repository.saveLocalPhoto(it) }
                                    if (compressedByteArray != null) {
                                        EventPhoto.Local()
                                    } else {
                                        photosDeleted++
                                        null
                                    }
                                } else {
                                    EventPhoto.Local(key = eventPhoto.key)
                                }
                            }
                            is UiEventPhoto.RemotePhoto -> {
                                eventPhoto.remoteEventPhoto
                            }
                            else -> {
                                null
                            }
                        }
                    }
                if (photosDeleted > 0) {
                    photosNotAddedToastMessageChannel.send(
                        UiText.Resource(
                            resId = R.string.photos_not_added,
                            args = arrayOf(photosDeleted)
                        )
                    )
                }
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
                    photos = eventPhotos,
                    attendees = attendees.value,
                    isCreator = savedStateHandle.get<AgendaItem.Event>("event")?.isCreator ?: true,
                    host = savedStateHandle.get<AgendaItem.Event>("event")?.host,
                    deletedPhotoKeys = emptyList(), // TODO setup deletedPhotosKeys
                    isGoing = savedStateHandle.get<AgendaItem.Event>("event")?.isGoing ?: true
                )
                repository.upsertEvent(event)
            }
        }
    }

    private val photosNotAddedToastMessageChannel = Channel<UiText>()
    val photosNotAddedToastMessage = photosNotAddedToastMessageChannel.receiveAsFlow()

    fun leaveEvent() {
    }

    fun deleteEvent() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                savedStateHandle.get<AgendaItem.Event>("event")?.let {
                    repository.deleteEvent(it)
                }
            }
        }
    }

    init {
        savedStateHandle.get<AgendaItem.Event>("event")?.let { item ->
            _isCreator.update { item.isCreator }
            setIsDone(item.isDone)
            setTitle(item.title)
            setDescription(item.description)
            setStartTime(item.startDateAndTime.toLocalTime())
            setStartDate(item.startDateAndTime.toLocalDate())
            setEndTime(item.endDateAndTime.toLocalTime())
            setEndDate(item.endDateAndTime.toLocalDate())
            setSelectedReminderTime(item.reminderTime)
            _photo.update {
                item.photos.filterIsInstance<EventPhoto.Remote>().map {
                    UiEventPhoto.RemotePhoto(it)
                }
            }
            _attendees.update { item.attendees }
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }

        val keysList = savedStateHandle.get<AgendaItem.Event>("event")?.photos?.map { it.key }
        viewModelScope.launch {
            if (keysList != null) {
                val internalStoragePhotos = repository.getLocalPhotos(keysList)
                internalStoragePhotos.forEach { internalStoragePhoto ->
                    _photo.update { list ->
                        list.toMutableList().plus(
                            UiEventPhoto.LocalPhoto(
                                key = internalStoragePhoto.key,
                                bitmap = BitmapConverters.byteArrayToBitmap(
                                    internalStoragePhoto.byteArray
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
