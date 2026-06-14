package com.example.reminderapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.data.preferences.PreferencesRepository
import com.example.reminderapp.notification.NotificationHelper.Companion.EXTRA_REMINDER_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: ReminderRepository
    @Inject lateinit var scheduler: ReminderScheduler
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var prefs: PreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
        if (reminderId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val reminder = repository.getById(reminderId) ?: return@launch
                if (!reminder.isActive) return@launch

                val soundEnabled = prefs.soundEnabled.first()
                notificationHelper.show(
                    reminder.id,
                    reminder.title,
                    reminder.description,
                    soundEnabled
                )

                if (reminder.recurrenceType == RecurrenceType.ONE_TIME) {
                    repository.update(reminder.copy(isActive = false))
                } else {
                    val next = reminder.copy(reminderDateTime = nextOccurrence(reminder))
                    repository.update(next)
                    scheduler.schedule(next)
                }
            } finally {
                pendingResult.finish()
            }
        }
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
}
