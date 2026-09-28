package com.example.reminderapp.ui.screen.edit

import com.example.reminderapp.domain.model.ReminderSound
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderType
import java.time.LocalDate
import java.time.LocalTime

data class EditUiState(
    val title: String = "",
    val description: String = "",
    val type: ReminderType = ReminderType.TASK,
    val date: LocalDate = LocalDate.now().plusDays(1),
    val time: LocalTime = LocalTime.of(9, 0),
    val recurrenceType: RecurrenceType = RecurrenceType.ONE_TIME,
    val sound: ReminderSound = ReminderSound.DEFAULT,
    val recurrenceInterval: Int? = null,
    val isLoading: Boolean = false,
    val titleError: Boolean = false,
    val dateError: Boolean = false,
    val isEditMode: Boolean = false
)

sealed interface EditUiEvent {
    data object OnCancelClicked : EditUiEvent
    data class OnTitleChange(val value: String) : EditUiEvent
    data class OnDescriptionChange(val value: String) : EditUiEvent
    data class OnTypeChange(val value: ReminderType) : EditUiEvent
    data class OnSoundChange(val value: ReminderSound) : EditUiEvent
    data class OnDateChange(val value: Long) : EditUiEvent
    data class OnTimeChange(val hour: Int, val minute: Int) : EditUiEvent
    data class OnRecurrenceTypeChange(val value: RecurrenceType) : EditUiEvent
    data class OnRecurrenceIntervalChange(val value: String) : EditUiEvent
    data object OnSave : EditUiEvent
    data object OnNavigateBack : EditUiEvent
}

sealed interface EditUiEffect {
    data object NavigateBack : EditUiEffect
}