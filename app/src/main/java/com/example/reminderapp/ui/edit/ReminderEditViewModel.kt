package com.example.reminderapp.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderType
import com.example.reminderapp.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

data class ReminderEditUiState(
    val title: String = "",
    val description: String = "",
    val type: ReminderType = ReminderType.TASK,
    val date: LocalDate = LocalDate.now().plusDays(1),
    val time: LocalTime = LocalTime.of(9, 0),
    val recurrenceType: RecurrenceType = RecurrenceType.ONE_TIME,
    val recurrenceInterval: Int? = null,
    val isLoading: Boolean = false,
    val titleError: Boolean = false,
    val dateError: Boolean = false
)

sealed interface EditUiEvent {
    data object NavigateBack : EditUiEvent
}

@HiltViewModel
class ReminderEditViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: Long? = savedStateHandle.get<Long>("id")
    val isEditMode: Boolean = reminderId != null

    private val _uiState = MutableStateFlow(ReminderEditUiState())
    val uiState: StateFlow<ReminderEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditUiEvent>()
    val events = _events.receiveAsFlow()

    private var originalCreatedAt: Long = System.currentTimeMillis()

    init {
        reminderId?.let { loadReminder(it) }
    }

    private fun loadReminder(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val reminder = repository.getById(id) ?: return@launch
            originalCreatedAt = reminder.createdAt
            val localDateTime = Instant.ofEpochMilli(reminder.reminderDateTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            _uiState.update {
                it.copy(
                    title = reminder.title,
                    description = reminder.description,
                    type = reminder.type,
                    date = localDateTime.toLocalDate(),
                    time = localDateTime.toLocalTime(),
                    recurrenceType = reminder.recurrenceType,
                    recurrenceInterval = reminder.recurrenceInterval,
                    isLoading = false
                )
            }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, titleError = false) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    fun onTypeChange(value: ReminderType) =
        _uiState.update { it.copy(type = value) }

    fun onDateChange(millis: Long) {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
        _uiState.update { it.copy(date = date, dateError = false) }
    }

    fun onTimeChange(hour: Int, minute: Int) =
        _uiState.update { it.copy(time = LocalTime.of(hour, minute), dateError = false) }

    fun onRecurrenceTypeChange(value: RecurrenceType) =
        _uiState.update {
            it.copy(
                recurrenceType = value,
                recurrenceInterval = if (value == RecurrenceType.CUSTOM) 1 else null
            )
        }

    fun onRecurrenceIntervalChange(value: String) =
        _uiState.update { it.copy(recurrenceInterval = value.filter(Char::isDigit).toIntOrNull()) }

    fun onSave() {
        val state = _uiState.value
        val reminderDateTime = state.date.atTime(state.time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val titleError = state.title.isBlank()
        val dateError = reminderDateTime <= System.currentTimeMillis()

        if (titleError || dateError) {
            _uiState.update { it.copy(titleError = titleError, dateError = dateError) }
            return
        }

        viewModelScope.launch {
            val reminder = Reminder(
                id = reminderId ?: 0,
                title = state.title.trim(),
                description = state.description.trim(),
                type = state.type,
                reminderDateTime = reminderDateTime,
                recurrenceType = state.recurrenceType,
                recurrenceInterval = state.recurrenceInterval,
                isActive = true,
                createdAt = originalCreatedAt
            )
            val scheduledReminder = if (isEditMode) {
                repository.update(reminder)
                reminder
            } else {
                val newId = repository.insert(reminder)
                reminder.copy(id = newId)
            }
            scheduler.schedule(scheduledReminder)
            _events.send(EditUiEvent.NavigateBack)
        }
    }

    fun onCancel() {
        viewModelScope.launch { _events.send(EditUiEvent.NavigateBack) }
    }
}
