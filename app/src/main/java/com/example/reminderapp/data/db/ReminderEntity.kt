package com.example.reminderapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderType

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val type: ReminderType,
    val reminderDateTime: Long,
    val recurrenceType: RecurrenceType,
    val recurrenceInterval: Int?,
    val isActive: Boolean,
    val createdAt: Long
)

fun ReminderEntity.toDomain() = Reminder(
    id = id,
    title = title,
    description = description,
    type = type,
    reminderDateTime = reminderDateTime,
    recurrenceType = recurrenceType,
    recurrenceInterval = recurrenceInterval,
    isActive = isActive,
    createdAt = createdAt
)

fun Reminder.toEntity() = ReminderEntity(
    id = id,
    title = title,
    description = description,
    type = type,
    reminderDateTime = reminderDateTime,
    recurrenceType = recurrenceType,
    recurrenceInterval = recurrenceInterval,
    isActive = isActive,
    createdAt = createdAt
)
