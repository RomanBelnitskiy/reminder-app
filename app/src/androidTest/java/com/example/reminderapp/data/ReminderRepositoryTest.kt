package com.example.reminderapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reminderapp.data.db.ReminderDatabase
import com.example.reminderapp.data.repository.ReminderRepository
import com.example.reminderapp.data.repository.ReminderRepositoryImpl
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.domain.model.ReminderType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {

    private lateinit var database: ReminderDatabase
    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ReminderDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ReminderRepositoryImpl(database.reminderDao())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun insertAndGetAllReminders() = runTest {
        val reminder = buildReminder(title = "Test")
        repository.insert(reminder)

        val all = repository.getAllReminders().first()
        assertEquals(1, all.size)
        assertEquals("Test", all[0].title)
    }

    @Test
    fun searchByTitle_returnsMatchingReminders() = runTest {
        repository.insert(buildReminder(title = "Birthday party"))
        repository.insert(buildReminder(title = "Doctor appointment"))

        val results = repository.searchByTitle("birthday").first()
        assertEquals(1, results.size)
        assertEquals("Birthday party", results[0].title)
    }

    @Test
    fun updateReminder_changesStoredValues() = runTest {
        val id = repository.insert(buildReminder(title = "Original"))
        val inserted = repository.getById(id)!!
        repository.update(inserted.copy(title = "Updated"))

        val updated = repository.getById(id)!!
        assertEquals("Updated", updated.title)
    }

    @Test
    fun deleteReminder_removesFromDb() = runTest {
        val id = repository.insert(buildReminder(title = "To delete"))
        val inserted = repository.getById(id)!!
        repository.delete(inserted)

        assertNull(repository.getById(id))
        assertTrue(repository.getAllReminders().first().isEmpty())
    }

    @Test
    fun getAllActive_returnsOnlyActiveReminders() = runTest {
        repository.insert(buildReminder(title = "Active", isActive = true))
        repository.insert(buildReminder(title = "Inactive", isActive = false))

        val active = repository.getAllActive()
        assertEquals(1, active.size)
        assertEquals("Active", active[0].title)
    }

    private fun buildReminder(
        title: String = "Reminder",
        isActive: Boolean = true
    ) = Reminder(
        title = title,
        description = "",
        type = ReminderType.TASK,
        reminderDateTime = System.currentTimeMillis() + 3_600_000L,
        recurrenceType = RecurrenceType.ONE_TIME,
        isActive = isActive
    )
}
