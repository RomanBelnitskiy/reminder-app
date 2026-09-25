package com.example.reminderapp.core.audio

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.reminderapp.domain.model.ReminderSound
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderAudioService : Service() {
    @Inject
    lateinit var soundPlayer: ReminderSoundPlayer

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(TAG, "onStartCommand: ${intent?.action}")
        when (intent?.action) {

            ACTION_PLAY -> {
                val sound = intent
                    .getStringExtra(EXTRA_SOUND)
                    ?.let(ReminderSound::valueOf)
                    ?: ReminderSound.DEFAULT

                soundPlayer.play(sound)
            }

            ACTION_STOP -> {
                soundPlayer.stop()
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        soundPlayer.stop()
        super.onDestroy()
    }

    companion object {
        const val ACTION_PLAY = "PLAY"
        const val ACTION_STOP = "STOP"

        const val EXTRA_SOUND = "sound"

        private val TAG = ReminderAudioService::class.java.simpleName
    }
}