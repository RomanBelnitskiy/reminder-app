package com.example.reminderapp.ui.ext

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal fun Long.toFormattedDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
