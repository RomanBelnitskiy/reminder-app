package com.example.reminderapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderListViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val reminders: StateFlow<List<Reminder>> = _searchQuery
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

    private var lastDeletedReminder: Reminder? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            lastDeletedReminder = reminder
            repository.delete(reminder)
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            lastDeletedReminder?.let {
                repository.insert(it)
                lastDeletedReminder = null
            }
        }
    }
}
