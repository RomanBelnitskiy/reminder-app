package com.example.reminderapp.fake

import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeReminderRepository : ReminderRepository {

    private val reminders = MutableStateFlow<List<Reminder>>(emptyList())
    private var nextId = 1L

    override fun getAllReminders(): Flow<List<Reminder>> =
        reminders.map { it.sortedBy(Reminder::reminderDateTime) }

    override fun searchByTitle(query: String): Flow<List<Reminder>> =
        reminders.map { list ->
            list.filter { it.title.contains(query, ignoreCase = true) }
                .sortedBy(Reminder::reminderDateTime)
        }

    override suspend fun getById(id: Long): Reminder? = reminders.value.find { it.id == id }

    override suspend fun getAllActive(): List<Reminder> = reminders.value.filter { it.isActive }

    override suspend fun insert(reminder: Reminder): Long {
        val id = if (reminder.id == 0L) nextId++ else reminder.id
        reminders.value = reminders.value + reminder.copy(id = id)
        return id
    }

    override suspend fun update(reminder: Reminder) {
        reminders.value = reminders.value.map { if (it.id == reminder.id) reminder else it }
    }

    override suspend fun delete(reminder: Reminder) {
        reminders.value = reminders.value.filter { it.id != reminder.id }
    }
}
