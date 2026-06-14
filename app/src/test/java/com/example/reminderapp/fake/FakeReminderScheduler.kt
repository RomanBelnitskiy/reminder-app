package com.example.reminderapp.fake

import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.notification.ReminderScheduler

class FakeReminderScheduler : ReminderScheduler {
    val scheduled = mutableListOf<Long>()
    val cancelled = mutableListOf<Long>()

    override fun schedule(reminder: Reminder) { scheduled += reminder.id }
    override fun cancel(reminderId: Long) { cancelled += reminderId }
}
