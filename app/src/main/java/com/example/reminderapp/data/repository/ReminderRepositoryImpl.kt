package com.example.reminderapp.data.repository

import com.example.reminderapp.data.db.ReminderDao
import com.example.reminderapp.data.db.toDomain
import com.example.reminderapp.data.db.toEntity
import com.example.reminderapp.domain.model.Reminder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao
) : ReminderRepository {

    override fun getAllReminders(): Flow<List<Reminder>> =
        dao.getAllReminders().map { list -> list.map { it.toDomain() } }

    override fun searchByTitle(query: String): Flow<List<Reminder>> =
        dao.searchByTitle(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Reminder? =
        dao.getById(id)?.toDomain()

    override suspend fun getAllActive(): List<Reminder> =
        dao.getAllActive().map { it.toDomain() }

    override suspend fun insert(reminder: Reminder): Long =
        dao.insert(reminder.toEntity())

    override suspend fun update(reminder: Reminder) =
        dao.update(reminder.toEntity())

    override suspend fun delete(reminder: Reminder) =
        dao.delete(reminder.toEntity())
}
