package com.example.reminderapp.domain.model

import androidx.annotation.StringRes
import com.example.reminderapp.R

enum class ReminderSound(@param:StringRes val labelRes: Int) {
    DEFAULT(R.string.reminder_sound_default),
    SOFT(R.string.reminder_sound_soft),
    LOUD(R.string.reminder_sound_loud)
}