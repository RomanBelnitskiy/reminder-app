package com.example.reminderapp.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.core.scheduler.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reminderId: Long = checkNotNull(savedStateHandle.get<Long>("id"))
    private val openedFromNotification: Boolean = checkNotNull(savedStateHandle.get<Boolean>("openedFromNotification"))

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _uiEffects = Channel<DetailUiEffect>()
    val effects = _uiEffects.receiveAsFlow()

    init {
        loadReminder()
    }

    private fun loadReminder() {
        viewModelScope.launch {
            val reminder = repository.getById(reminderId)
            _uiState.update { it.copy(reminder = reminder, isLoading = false) }
        }
    }

    fun processUiEvent(event: DetailUiEvent) {
        when(event) {
            DetailUiEvent.OnBackClick -> onBackClick()
            DetailUiEvent.OnToggleActive -> toggleActive()
            DetailUiEvent.OnDeleteClick -> onDeleteClick()
            is DetailUiEvent.OnEditClick -> onEditClick()
            DetailUiEvent.OnDeleteDialogDismiss -> onDeleteDialogDismiss()
            DetailUiEvent.OnDeleteConfirm -> onDeleteConfirm()
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            _uiEffects.send(DetailUiEffect.NavigateBack)
        }
    }

    private fun toggleActive() {
        viewModelScope.launch {
            val reminder = _uiState.value.reminder ?: return@launch
            val updated = reminder.copy(isActive = !reminder.isActive)
            repository.update(updated)
            if (updated.isActive) scheduler.schedule(updated) else scheduler.cancel(reminder.id)
            _uiState.update { it.copy(reminder = updated) }
        }
    }

    private fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }

    private fun onEditClick() {
        viewModelScope.launch {
            _uiEffects.send(
                DetailUiEffect.NavigateToEdit(reminderId)
            )
        }
    }

    private fun onDeleteDialogDismiss() = _uiState.update { it.copy(showDeleteDialog = false) }

    private fun onDeleteConfirm() {
        viewModelScope.launch {
            _uiState.value.reminder?.let { repository.delete(it) }
            scheduler.cancel(reminderId)
            _uiEffects.send(DetailUiEffect.NavigateBack)
        }
    }
}
