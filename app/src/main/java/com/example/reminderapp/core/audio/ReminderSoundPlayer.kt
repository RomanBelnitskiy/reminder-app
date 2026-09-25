package com.example.reminderapp.core.audio

import com.example.reminderapp.domain.model.ReminderSound

interface ReminderSoundPlayer {

    fun play(sound: ReminderSound)

    fun stop()

    fun release()
}