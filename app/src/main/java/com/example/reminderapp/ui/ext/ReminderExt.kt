package com.example.reminderapp.ui.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.reminderapp.R
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder

@Composable
internal fun Reminder.recurrenceLabel(): String {
    if (recurrenceType == RecurrenceType.CUSTOM && recurrenceInterval != null) {
        return stringResource(R.string.detail_interval_format, recurrenceInterval)
    }
    return stringResource(recurrenceType.labelRes)
}
