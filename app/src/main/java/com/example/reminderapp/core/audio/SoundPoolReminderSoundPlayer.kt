package com.example.reminderapp.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.reminderapp.R
import com.example.reminderapp.domain.model.ReminderSound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundPoolReminderSoundPlayer @Inject constructor(
    @field:ApplicationContext val context: Context
) : ReminderSoundPlayer {

    private val soundsMap: Map<ReminderSound, Int> = mapOf(
            ReminderSound.DEFAULT to R.raw.default_alarm,
            ReminderSound.SOFT to R.raw.soft_alarm,
            ReminderSound.LOUD to R.raw.loud_alarm
        )

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    private val soundPool = SoundPool.Builder()
        .setAudioAttributes(audioAttributes)
        .setMaxStreams(MAX_STREAMS)
        .build()

    private val loadedSounds = mutableMapOf<ReminderSound, Int>()
    private val pendingLoads = mutableMapOf<Int, ReminderSound>()
    private val failedSounds = mutableSetOf<ReminderSound>()
    private var pendingPlayback: ReminderSound? = null
    private var initialized = false
    private var currentStreamId: Int? = null

    init {
        initialize()
    }

    private fun initialize() {
        if (initialized) return
        initialized = true

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->

            val sound = pendingLoads.remove(sampleId)
                ?: return@setOnLoadCompleteListener

            if (status == STATUS_LOAD_SUCCESS) {
                loadedSounds[sound] = sampleId

                if (pendingPlayback == sound) {
                    pendingPlayback = null
                    playLoaded(sampleId)
                }
            } else {
                failedSounds += sound
                Log.d(TAG, "Failed to load reminder sound: $sound, status=$status")
            }
        }

        soundsMap.forEach { (sound, resourceId) ->
            val soundId = soundPool.load(
                context,
                resourceId,
                LOAD_PRIORITY
            )

            pendingLoads[soundId] = sound
        }
    }


    override fun play(sound: ReminderSound) {
        if (!initialized) {
            initialize()
        }

        val soundId = loadedSounds[sound]

        if (soundId == null) {
            pendingPlayback = sound
            return
        }

        playLoaded(soundId)
    }

    private fun playLoaded(soundId: Int) {
        stop()

        currentStreamId = soundPool.play(
            soundId,
            LEFT_VOLUME,
            RIGHT_VOLUME,
            PLAY_PRIORITY,
            LOOP_FOREVER,
            PLAYBACK_RATE
        )
    }

    override fun stop() {
        stopCurrentStream()
        pendingPlayback = null
    }

    private fun stopCurrentStream() {
        currentStreamId?.let(soundPool::stop)
        currentStreamId = null
    }

    override fun release() {
        stop()

        soundPool.release()

        loadedSounds.clear()
        pendingLoads.clear()
        failedSounds.clear()

        initialized = false
    }

    companion object {
        private const val MAX_STREAMS = 1
        private const val STATUS_LOAD_SUCCESS = 0

        private const val LEFT_VOLUME = 1f
        private const val RIGHT_VOLUME = 1f
        private const val PLAY_PRIORITY = 1
        private const val LOOP_FOREVER = -1
        private const val PLAYBACK_RATE = 1f

        private const val LOAD_PRIORITY = 1

        private val TAG = SoundPoolReminderSoundPlayer::class.java.simpleName
    }
}