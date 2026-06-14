package com.example.reminderapp.notification

import com.example.reminderapp.domain.model.Reminder

interface ReminderScheduler {
    fun schedule(reminder: Reminder)
    fun cancel(reminderId: Long)
}
