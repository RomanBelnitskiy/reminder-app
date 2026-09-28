package com.example.reminderapp.ui.screen.detail

import com.example.reminderapp.domain.model.Reminder

data class DetailUiState(
    val reminder: Reminder? = null,
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false
)

sealed interface DetailUiEvent {
    data object OnBackClick : DetailUiEvent
    data object OnToggleActive : DetailUiEvent
    data object OnEditClick : DetailUiEvent
    data object OnDeleteClick : DetailUiEvent
    data object OnDeleteDialogDismiss : DetailUiEvent
    data object OnDeleteConfirm : DetailUiEvent
}

sealed interface DetailUiEffect {
    data object NavigateBack : DetailUiEffect
    data class NavigateToEdit(val reminderId: Long) : DetailUiEffect
}