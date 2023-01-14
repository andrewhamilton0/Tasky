package com.andrew.tasky.agenda.presentation.screens.reminder_detail

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.domain.repository.AgendaItemRepository
import com.andrew.tasky.agenda.util.AgendaItemType
import com.andrew.tasky.agenda.util.ReminderTime
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
class ReminderDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AgendaItemRepository
) : ViewModel() {

    private val agendaItemType = AgendaItemType.REMINDER

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

    private val _selectedReminderTime = MutableStateFlow(ReminderTime.TEN_MINUTES_BEFORE)
    val selectedReminderTime = _selectedReminderTime.asStateFlow()
    fun setSelectedReminderTime(selectedReminderTime: ReminderTime) {
        _selectedReminderTime.value = selectedReminderTime
    }

    fun saveAgendaItem() {
        val agendaItem = AgendaItem.Reminder(
            id = savedStateHandle.get<AgendaItem.Reminder>("agendaItem")?.id,
            isDone = isDone.value,
            title = title.value,
            description = description.value,
            startDateAndTime = LocalDateTime.of(
                selectedStartDate.value,
                selectedStartTime.value
            ),
            reminderTime = selectedReminderTime.value
        )
        viewModelScope.launch {
            withContext(NonCancellable) {
                repository.upsert(agendaItem)
            }
        }
    }

    fun deleteAgendaItem() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                savedStateHandle.get<AgendaItem.Reminder>("agendaItem")?.let {
                    repository.deleteAgendaItem(it)
                }
            }
        }
    }

    init {
        savedStateHandle.get<AgendaItem.Reminder>("agendaItem")?.let { item ->
            setIsDone(item.isDone)
            setTitle(item.title)
            setDescription(item.description)
            setStartTime(item.startDateAndTime.toLocalTime())
            setStartDate(item.startDateAndTime.toLocalDate())
            setSelectedReminderTime(item.reminderTime)
        }
        savedStateHandle.get<Boolean>("isInEditMode")?.let { initialEditMode ->
            setEditMode(initialEditMode)
        }
    }
}
