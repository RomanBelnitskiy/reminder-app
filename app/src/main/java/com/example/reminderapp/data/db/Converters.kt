package com.example.reminderapp.data.db

import androidx.room.TypeConverter
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderType

class Converters {

    @TypeConverter
    fun fromReminderType(value: ReminderType): String = value.name

    @TypeConverter
    fun toReminderType(value: String): ReminderType = ReminderType.valueOf(value)

    @TypeConverter
    fun fromRecurrenceType(value: RecurrenceType): String = value.name

    @TypeConverter
    fun toRecurrenceType(value: String): RecurrenceType = RecurrenceType.valueOf(value)
}
