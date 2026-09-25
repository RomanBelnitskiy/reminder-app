package com.example.reminderapp.domain.model

data class ReminderTriggerResult(
    val reminder: Reminder,
    val soundEnabled: Boolean
)
