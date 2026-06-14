package com.example.reminderapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.reminderapp.MainActivity
import com.example.reminderapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "reminders_channel"
        const val EXTRA_REMINDER_ID = "reminder_id"
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    fun show(reminderId: Long, title: String, description: String, soundEnabled: Boolean = true) {
        if (!NotificationPermissionHelper.hasPermission(context)) {
            return
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_REMINDER_ID, reminderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaults = if (soundEnabled) {
            NotificationCompat.DEFAULT_ALL
        } else {
            NotificationCompat.DEFAULT_VIBRATE or NotificationCompat.DEFAULT_LIGHTS
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description.ifEmpty { context.getString(R.string.notification_default_text) })
            .setAutoCancel(true)
            .setDefaults(defaults)
            .setContentIntent(pendingIntent)
            .addAction(0, context.getString(R.string.notification_action_open), pendingIntent)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(reminderId.toInt(), notification)
    }
}
