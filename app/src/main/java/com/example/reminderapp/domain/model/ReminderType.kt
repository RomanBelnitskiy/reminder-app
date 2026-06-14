package com.example.reminderapp.domain.model

import androidx.annotation.StringRes
import com.example.reminderapp.R

enum class ReminderType(@param:StringRes val labelRes: Int) {
    BIRTHDAY(R.string.reminder_type_birthday),
    MEETING(R.string.reminder_type_meeting),
    BILL(R.string.reminder_type_bill),
    MEDICINE(R.string.reminder_type_medicine),
    TASK(R.string.reminder_type_task),
    ANNIVERSARY(R.string.reminder_type_anniversary)
}
