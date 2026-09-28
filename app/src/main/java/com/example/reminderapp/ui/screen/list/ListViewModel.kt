package com.example.reminderapp.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _uiEffects = Channel<ListUiEffect>()
    val effects = _uiEffects.receiveAsFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val reminders: StateFlow<List<Reminder>> = _uiState
        .map { it.searchQuery }
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank())
                repository.getAllReminders()
            else repository.searchByTitle(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun processUiEvent(event: ListUiEvent) {
        when(event) {
            is ListUiEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            is ListUiEvent.OnDeleteReminder -> onDeleteReminder(event.reminder)
            ListUiEvent.OnUndoDeleteReminder -> onUndoDelete()
            ListUiEvent.OnSettingsClick -> onSettingsClick()
            ListUiEvent.OnCreateClick -> onCreateClick()
            is ListUiEvent.OnReminderClick -> onReminderClick(event.reminderId)
        }
    }

    private fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    private fun onDeleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            _uiState.update { it.copy(lastDeletedReminder = reminder) }
            repository.delete(reminder)
        }
    }

    private fun onUndoDelete() {
        viewModelScope.launch {
            _uiState.value.lastDeletedReminder?.let { reminder ->
                repository.insert(reminder)
                _uiState.update { it.copy(lastDeletedReminder = null) }
            }
        }
    }

    private fun onSettingsClick() {
        viewModelScope.launch {
            _uiEffects.send(ListUiEffect.NavigateToSettings)
        }
    }

    private fun onCreateClick() {
        viewModelScope.launch {
            _uiEffects.send(ListUiEffect.NavigateToCreate)
        }
    }

    private fun onReminderClick(reminderId: Long) {
        viewModelScope.launch {
            _uiEffects.send(ListUiEffect.NavigateToDetail(reminderId))
        }
    }
}
