package com.example.reminderapp.domain.model

import androidx.annotation.StringRes
import com.example.reminderapp.R

enum class RecurrenceType(@param:StringRes val labelRes: Int) {
    ONE_TIME(R.string.recurrence_type_one_time),
    DAILY(R.string.recurrence_type_daily),
    WEEKLY(R.string.recurrence_type_weekly),
    MONTHLY(R.string.recurrence_type_monthly),
    YEARLY(R.string.recurrence_type_yearly),
    CUSTOM(R.string.recurrence_type_custom)
}
