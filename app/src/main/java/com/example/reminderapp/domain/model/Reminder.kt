package com.example.reminderapp.domain.model

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: ReminderType,
    val reminderDateTime: Long,
    val recurrenceType: RecurrenceType,
    val recurrenceInterval: Int? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
