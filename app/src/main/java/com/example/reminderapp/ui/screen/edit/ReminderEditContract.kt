package com.example.reminderapp.ui.screen.edit

import com.example.reminderapp.domain.model.ReminderSound
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderType
import java.time.LocalDate
import java.time.LocalTime

data class ReminderEditUiState(
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

sealed interface ReminderEditUiEvent {
    data object OnCancelClicked : ReminderEditUiEvent
    data class OnTitleChange(val value: String) : ReminderEditUiEvent
    data class OnDescriptionChange(val value: String) : ReminderEditUiEvent
    data class OnTypeChange(val value: ReminderType) : ReminderEditUiEvent
    data class OnSoundChange(val value: ReminderSound) : ReminderEditUiEvent
    data class OnDateChange(val value: Long) : ReminderEditUiEvent
    data class OnTimeChange(val hour: Int, val minute: Int) : ReminderEditUiEvent
    data class OnRecurrenceTypeChange(val value: RecurrenceType) : ReminderEditUiEvent
    data class OnRecurrenceIntervalChange(val value: String) : ReminderEditUiEvent
    data object OnSave : ReminderEditUiEvent
    data object OnNavigateBack : ReminderEditUiEvent
}

sealed interface ReminderEditUiEffect {
    data object NavigateBack : ReminderEditUiEffect
}