package com.example.reminderapp.ui.screen.list

import com.example.reminderapp.domain.model.Reminder

data class ListUiState(
    val searchQuery: String = "",
    val lastDeletedReminder: Reminder? = null
)

sealed interface ListUiEvent {
    data class OnSearchQueryChange(val query: String) : ListUiEvent
    data class OnDeleteReminder(val reminder: Reminder) : ListUiEvent
    data object OnUndoDeleteReminder : ListUiEvent
    data object OnSettingsClick : ListUiEvent
    data object OnCreateClick : ListUiEvent
    data class OnReminderClick(val reminderId: Long) : ListUiEvent
}

sealed interface ListUiEffect {
    data class NavigateToDetail(val reminderId: Long) : ListUiEffect
    data object NavigateToCreate : ListUiEffect
    data object NavigateToSettings : ListUiEffect
}