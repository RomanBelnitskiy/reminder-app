package com.example.reminderapp.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.reminderapp.core.audio.AudioController
import com.example.reminderapp.core.audio.ReminderAudio
import com.example.reminderapp.core.audio.ReminderAudioService
import com.example.reminderapp.core.notification.NotificationHelper
import com.example.reminderapp.domain.usecase.ReminderHandlerUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderHandlerUseCase: ReminderHandlerUseCase

    @Inject
    lateinit var audioController: AudioController

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(NotificationHelper.EXTRA_REMINDER_ID, -1L)
        Log.d(TAG, "onReceive-> reminderId: $reminderId")
        if (reminderId == -1L) return


        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val result = reminderHandlerUseCase(reminderId)

                if (result != null) {

                    if (result.soundEnabled) {
                        audioController.play(ReminderAudio.soft(), context)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private val TAG = ReminderReceiver::class.java.simpleName
    }
}