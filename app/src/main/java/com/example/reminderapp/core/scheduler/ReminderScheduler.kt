package com.example.reminderapp.core.scheduler

import com.example.reminderapp.domain.model.Reminder

interface ReminderScheduler {

    fun schedule(reminder: Reminder)

    fun cancel(reminderId: Long)

    fun reschedule(reminder: Reminder)

}
