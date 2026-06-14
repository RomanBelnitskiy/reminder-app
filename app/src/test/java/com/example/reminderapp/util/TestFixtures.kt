package com.example.reminderapp.util

import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.ReminderType

fun testReminder(
    id: Long = 0L,
    title: String = "Test reminder",
    description: String = "Description",
    type: ReminderType = ReminderType.TASK,
    reminderDateTime: Long = System.currentTimeMillis() + 3_600_000L,
    recurrenceType: RecurrenceType = RecurrenceType.ONE_TIME,
    recurrenceInterval: Int? = null,
    isActive: Boolean = true
) = Reminder(
    id = id,
    title = title,
    description = description,
    type = type,
    reminderDateTime = reminderDateTime,
    recurrenceType = recurrenceType,
    recurrenceInterval = recurrenceInterval,
    isActive = isActive
)
