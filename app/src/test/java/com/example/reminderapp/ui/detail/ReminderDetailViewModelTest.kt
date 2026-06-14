package com.example.reminderapp.ui.detail

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import androidx.lifecycle.SavedStateHandle
import com.example.reminderapp.fake.FakeReminderRepository
import com.example.reminderapp.fake.FakeReminderScheduler
import com.example.reminderapp.util.MainDispatcherExtension
import com.example.reminderapp.util.testReminder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ReminderDetailViewModelTest {

    private lateinit var repository: FakeReminderRepository
    private lateinit var scheduler: FakeReminderScheduler

    private fun createViewModel(id: Long): ReminderDetailViewModel =
        ReminderDetailViewModel(
            repository = repository,
            scheduler = scheduler,
            savedStateHandle = SavedStateHandle(mapOf("id" to id))
        )

    @BeforeEach
    fun setup() {
        repository = FakeReminderRepository()
        scheduler = FakeReminderScheduler()
    }

    @Test
    fun `loads reminder by id`() = runTest {
        repository.insert(testReminder(id = 1L, title = "Dentist"))
        val viewModel = createViewModel(id = 1L)
        assertThat(viewModel.uiState.value.reminder?.title).isEqualTo("Dentist")
        assertThat(viewModel.uiState.value.isLoading).isEqualTo(false)
    }

    @Test
    fun `toggleActive deactivates active reminder and cancels alarm`() = runTest {
        repository.insert(testReminder(id = 1L, isActive = true))
        val viewModel = createViewModel(id = 1L)

        viewModel.toggleActive()

        assertThat(viewModel.uiState.value.reminder?.isActive).isEqualTo(false)
        assertThat(scheduler.cancelled).contains(1L)
    }

    @Test
    fun `toggleActive activates inactive reminder and schedules alarm`() = runTest {
        repository.insert(testReminder(id = 1L, isActive = false))
        val viewModel = createViewModel(id = 1L)

        viewModel.toggleActive()

        assertThat(viewModel.uiState.value.reminder?.isActive).isEqualTo(true)
        assertThat(scheduler.scheduled).contains(1L)
    }

    @Test
    fun `onDeleteConfirm deletes reminder and emits NavigateBack`() = runTest {
        repository.insert(testReminder(id = 1L))
        val viewModel = createViewModel(id = 1L)

        viewModel.events.test {
            viewModel.onDeleteConfirm()
            assertThat(awaitItem()).isEqualTo(DetailUiEvent.NavigateBack)
        }

        assertThat(repository.getAllActive()).isEmpty()
        assertThat(scheduler.cancelled).contains(1L)
    }

    @Test
    fun `onDeleteClick shows dialog, dismiss hides it`() = runTest {
        repository.insert(testReminder(id = 1L))
        val viewModel = createViewModel(id = 1L)

        viewModel.onDeleteClick()
        assertThat(viewModel.uiState.value.showDeleteDialog).isEqualTo(true)

        viewModel.onDeleteDialogDismiss()
        assertThat(viewModel.uiState.value.showDeleteDialog).isEqualTo(false)
    }
}
