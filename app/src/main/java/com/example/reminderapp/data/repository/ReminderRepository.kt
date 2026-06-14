package com.example.reminderapp.data.repository

import com.example.reminderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getAllReminders(): Flow<List<Reminder>>
    fun searchByTitle(query: String): Flow<List<Reminder>>
    suspend fun getById(id: Long): Reminder?
    suspend fun getAllActive(): List<Reminder>
    suspend fun insert(reminder: Reminder): Long
    suspend fun update(reminder: Reminder)
    suspend fun delete(reminder: Reminder)
}
