package com.example.reminderapp.core.audio

import android.net.Uri
import com.example.reminderapp.domain.model.ReminderSound

interface ReminderAudio {

    data class BuiltIn(
        val sound: ReminderSound
    ) : ReminderAudio {

        override fun toString(): String {
            return sound.toString()
        }

    }

    data class File(
        val uri: Uri
    ) : ReminderAudio {

        override fun toString(): String {
            return uri.toString()
        }
    }

    companion object {
        fun default() = BuiltIn(ReminderSound.DEFAULT)
        fun soft() = BuiltIn(ReminderSound.SOFT)
        fun loud() = BuiltIn(ReminderSound.LOUD)
        fun file(uri: Uri) = File(uri)
    }
}