package com.example.reminderapp.ui.screen.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.core.scheduler.ReminderScheduler
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.ReminderSound
import com.example.reminderapp.domain.model.ReminderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject


@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: Long? = savedStateHandle.get<Long>("id")

    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val _effects = Channel<EditUiEffect>()
    val effects = _effects.receiveAsFlow()

    private var originalCreatedAt: Long = System.currentTimeMillis()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isEditMode = reminderId != null) }
        }
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
                    sound = reminder.sound,
                    isLoading = false
                )
            }
        }
    }

    fun processUiEvent(event: EditUiEvent) {
        when(event) {
            EditUiEvent.OnCancelClicked -> onNavigateBack()
            EditUiEvent.OnSave -> onSave()
            EditUiEvent.OnNavigateBack -> onNavigateBack()
            is EditUiEvent.OnTitleChange -> onTitleChange(event.value)
            is EditUiEvent.OnDescriptionChange -> onDescriptionChange(event.value)
            is EditUiEvent.OnTypeChange -> onTypeChange(event.value)
            is EditUiEvent.OnSoundChange -> onSoundChange(event.value)
            is EditUiEvent.OnDateChange -> onDateChange(event.value)
            is EditUiEvent.OnTimeChange -> onTimeChange(event.hour, event.minute)
            is EditUiEvent.OnRecurrenceTypeChange -> onRecurrenceTypeChange(event.value)
            is EditUiEvent.OnRecurrenceIntervalChange -> onRecurrenceIntervalChange(event.value)
        }
    }

    private fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, titleError = false) }

    private fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    private fun onTypeChange(value: ReminderType) =
        _uiState.update { it.copy(type = value) }

    private fun onSoundChange(value: ReminderSound) =
        _uiState.update { it.copy(sound = value) }

    private fun onDateChange(millis: Long) {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
        _uiState.update { it.copy(date = date, dateError = false) }
    }

    private fun onTimeChange(hour: Int, minute: Int) =
        _uiState.update { it.copy(time = LocalTime.of(hour, minute), dateError = false) }

    private fun onRecurrenceTypeChange(value: RecurrenceType) =
        _uiState.update {
            it.copy(
                recurrenceType = value,
                recurrenceInterval = if (value == RecurrenceType.CUSTOM) 1 else null
            )
        }

    private fun onRecurrenceIntervalChange(value: String) =
        _uiState.update { it.copy(recurrenceInterval = value.filter(Char::isDigit).toIntOrNull()) }

    private fun onSave() {
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
                sound = state.sound,
                recurrenceInterval = state.recurrenceInterval,
                isActive = true,
                createdAt = originalCreatedAt
            )
            val scheduledReminder = if (_uiState.value.isEditMode) {
                repository.update(reminder)
                reminder
            } else {
                val newId = repository.insert(reminder)
                reminder.copy(id = newId)
            }
            scheduler.schedule(scheduledReminder)
            _effects.send(EditUiEffect.NavigateBack)
        }
    }

    private fun onNavigateBack() {
        viewModelScope.launch {
            _effects.send(EditUiEffect.NavigateBack)
        }
    }
}
