package com.example.reminderapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val reminder: Reminder? = null,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

sealed interface DetailUiEvent {
    data object NavigateBack : DetailUiEvent
}

@HiltViewModel
class ReminderDetailViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: Long = checkNotNull(savedStateHandle.get<Long>("id"))

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<DetailUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadReminder()
    }

    private fun loadReminder() {
        viewModelScope.launch {
            val reminder = repository.getById(reminderId)
            _uiState.update { it.copy(reminder = reminder, isLoading = false) }
        }
    }

    fun toggleActive() {
        viewModelScope.launch {
            val reminder = _uiState.value.reminder ?: return@launch
            val updated = reminder.copy(isActive = !reminder.isActive)
            repository.update(updated)
            if (updated.isActive) scheduler.schedule(updated) else scheduler.cancel(reminder.id)
            _uiState.update { it.copy(reminder = updated) }
        }
    }

    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }

    fun onDeleteDialogDismiss() = _uiState.update { it.copy(showDeleteDialog = false) }

    fun onDeleteConfirm() {
        viewModelScope.launch {
            _uiState.value.reminder?.let { repository.delete(it) }
            scheduler.cancel(reminderId)
            _events.send(DetailUiEvent.NavigateBack)
        }
    }
}
