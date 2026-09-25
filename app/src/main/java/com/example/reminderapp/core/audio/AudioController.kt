package com.example.reminderapp.core.audio

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.reminderapp.core.audio.ReminderAudioService.Companion.ACTION_PLAY
import com.example.reminderapp.core.audio.ReminderAudioService.Companion.ACTION_STOP
import com.example.reminderapp.core.audio.ReminderAudioService.Companion.EXTRA_SOUND
import javax.inject.Inject


class AudioController @Inject constructor() {

    fun play(audio: ReminderAudio, context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, ReminderAudioService::class.java).apply {
                action = ACTION_PLAY
                putExtra(EXTRA_SOUND, audio.toString())
            }
        )
    }

    fun stop(context: Context) {
        context.startService(
            Intent(context, ReminderAudioService::class.java).apply {
                action = ACTION_STOP
            }
        )
    }
}