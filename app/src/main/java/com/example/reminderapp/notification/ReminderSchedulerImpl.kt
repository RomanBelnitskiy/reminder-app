package com.example.reminderapp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.notification.NotificationHelper.Companion.EXTRA_REMINDER_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(reminder: Reminder) {
        val pendingIntent = buildPendingIntent(
            reminder.id,
            PendingIntent.FLAG_UPDATE_CURRENT
        ) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.reminderDateTime,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.reminderDateTime,
                pendingIntent
            )
        }
    }

    override fun cancel(reminderId: Long) {
        buildPendingIntent(reminderId, PendingIntent.FLAG_NO_CREATE)?.let {
            alarmManager.cancel(it)
        }
    }

    private fun buildPendingIntent(reminderId: Long, flags: Int): PendingIntent? =
        PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            Intent(context, ReminderReceiver::class.java)
                .putExtra(EXTRA_REMINDER_ID, reminderId),
            flags or PendingIntent.FLAG_IMMUTABLE
        )
}
