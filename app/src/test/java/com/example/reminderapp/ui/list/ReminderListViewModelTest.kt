package com.example.reminderapp.ui.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.example.reminderapp.fake.FakeReminderRepository
import com.example.reminderapp.util.MainDispatcherExtension
import com.example.reminderapp.util.testReminder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class ReminderListViewModelTest {

    private lateinit var repository: FakeReminderRepository
    private lateinit var viewModel: ReminderListViewModel

    @BeforeEach
    fun setup() {
        repository = FakeReminderRepository()
        viewModel = ReminderListViewModel(repository)
    }

    @Test
    fun `initial reminders list is empty`() = runTest {
        viewModel.reminders.test {
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reminders list updates when item is inserted`() = runTest {
        viewModel.reminders.test {
            awaitItem() // initial emptyList from stateIn
            advanceTimeBy(301L) // pass the debounce so upstream activates
            repository.insert(testReminder(title = "Buy milk"))
            assertThat(awaitItem()).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `searchQuery filters reminders by title`() = runTest {
        repository.insert(testReminder(title = "Birthday party"))
        repository.insert(testReminder(title = "Doctor appointment"))

        viewModel.reminders.test {
            awaitItem() // initial emptyList
            advanceTimeBy(301L) // activate upstream for empty query
            awaitItem() // now gets all 2 reminders

            viewModel.onSearchQueryChange("birth")
            advanceTimeBy(301L) // pass debounce for new query

            val result = awaitItem()
            assertThat(result).hasSize(1)
            assertThat(result[0].title).isEqualTo("Birthday party")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteReminder removes item from list`() = runTest {
        val reminder = testReminder(id = 1L)

        viewModel.reminders.test {
            awaitItem() // initial emptyList
            advanceTimeBy(301L) // activate upstream
            repository.insert(reminder)
            assertThat(awaitItem()).hasSize(1)
            viewModel.deleteReminder(reminder)
            assertThat(awaitItem()).isEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `undoDelete restores deleted reminder`() = runTest {
        val reminder = testReminder(id = 1L)

        viewModel.reminders.test {
            awaitItem() // initial
            advanceTimeBy(301L)
            repository.insert(reminder)
            awaitItem() // [reminder]
            viewModel.deleteReminder(reminder)
            awaitItem() // []
            viewModel.undoDelete()
            assertThat(awaitItem()).hasSize(1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
