package com.andrew.tasky.agenda.presentation.screens.task_detail

import androidx.lifecycle.*
import com.andrew.tasky.agenda.domain.TaskRepository
import com.andrew.tasky.agenda.domain.models.AgendaItem
import com.andrew.tasky.agenda.util.ReminderTime
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaskRepository,
) : ViewModel() {

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
        val task = AgendaItem.Task(
            id = savedStateHandle
                .get<String>("id") ?: UUID.randomUUID().toString(),
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
                if (savedStateHandle.get<String>("id") == null) {
                    repository.createTask(task)
                } else {
                    repository.updateTask(task)
                }
            }
        }
    }

    fun deleteAgendaItem() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                savedStateHandle.get<String>("id")?.let {
                    repository.deleteTask(it)
                }
            }
        }
    }

    init {
        savedStateHandle.get<String>("id")?.let { taskId ->
            viewModelScope.launch {
                repository.getTask(taskId)?.let { task ->
                    setIsDone(task.isDone)
                    setTitle(task.title)
                    setDescription(task.description)
                    setStartTime(task.startDateAndTime.toLocalTime())
                    setStartDate(task.startDateAndTime.toLocalDate())
                    setSelectedReminderTime(task.reminderTime)
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
