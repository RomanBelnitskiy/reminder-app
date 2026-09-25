package com.example.reminderapp.domain.usecase

import android.util.Log
import com.example.reminderapp.core.notification.NotificationHelper
import com.example.reminderapp.core.scheduler.ReminderScheduler
import com.example.reminderapp.data.preferences.PreferencesRepository
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.ReminderTriggerResult
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class ReminderHandlerUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler,
    private val notificationHelper: NotificationHelper,
    private val prefs: PreferencesRepository
) {

    suspend operator fun invoke(reminderId: Long): ReminderTriggerResult? {
        Log.d(TAG, "reminderId: $reminderId")

        val reminder = repository.getById(reminderId) ?: return null
        if (!reminder.isActive) return null

        notificationHelper.showReminder(reminder)
        val soundEnabled = prefs.soundEnabled.first()

        // TODO: should be moved to reminder screen and do something after user interaction
        if (reminder.recurrenceType == RecurrenceType.ONE_TIME) {
            repository.update(reminder.copy(isActive = false))
        } else {
            val next = reminder.copy(reminderDateTime = nextOccurrence(reminder))
            repository.update(next)
            scheduler.schedule(next)
        }

        return ReminderTriggerResult(
            reminder,
            soundEnabled
        )
    }

    private fun nextOccurrence(reminder: Reminder): Long {
        val dt = Instant.ofEpochMilli(reminder.reminderDateTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val next = when (reminder.recurrenceType) {
            RecurrenceType.DAILY -> dt.plusDays(1)
            RecurrenceType.WEEKLY -> dt.plusWeeks(1)
            RecurrenceType.MONTHLY -> dt.plusMonths(1)
            RecurrenceType.YEARLY -> dt.plusYears(1)
            RecurrenceType.CUSTOM -> dt.plusDays(reminder.recurrenceInterval?.toLong() ?: 1)
            RecurrenceType.ONE_TIME -> dt
        }

        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    companion object {
        private val TAG = ReminderHandlerUseCase::class.java.simpleName
    }
}