package com.example.reminderapp.ui.edit

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
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
class ReminderEditViewModelTest {

    private lateinit var repository: FakeReminderRepository
    private lateinit var scheduler: FakeReminderScheduler

    private fun createViewModel(id: Long? = null): ReminderEditViewModel =
        ReminderEditViewModel(
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
    fun `create mode starts with blank title and no errors`() = runTest {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertThat(state.title).isEqualTo("")
        assertThat(state.titleError).isEqualTo(false)
        assertThat(state.dateError).isEqualTo(false)
        assertThat(viewModel.isEditMode).isEqualTo(false)
    }

    @Test
    fun `save with blank title sets titleError`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTitleChange("")
        viewModel.onSave()
        assertThat(viewModel.uiState.value.titleError).isEqualTo(true)
    }

    @Test
    fun `save with past date sets dateError`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTitleChange("Reminder")
        // Set a past date via direct millis (just past midnight of 2000-01-01 UTC)
        viewModel.onDateChange(946684800000L)
        viewModel.onSave()
        assertThat(viewModel.uiState.value.dateError).isEqualTo(true)
    }

    @Test
    fun `valid save in create mode inserts reminder and emits NavigateBack`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTitleChange("Meeting")
        // Date is default: now + 1 day, which is valid

        viewModel.events.test {
            viewModel.onSave()
            assertThat(awaitItem()).isEqualTo(EditUiEvent.NavigateBack)
        }

        assertThat(repository.getAllActive()).hasSize(1)
        assertThat(scheduler.scheduled).hasSize(1)
    }

    @Test
    fun `edit mode loads existing reminder`() = runTest {
        val original = testReminder(id = 1L, title = "Birthday")
        repository.insert(original)

        val viewModel = createViewModel(id = 1L)
        // Wait for loading to complete
        assertThat(viewModel.uiState.value.title).isEqualTo("Birthday")
        assertThat(viewModel.isEditMode).isEqualTo(true)
    }

    @Test
    fun `cancel emits NavigateBack without saving`() = runTest {
        val viewModel = createViewModel()
        viewModel.onTitleChange("Should not save")

        viewModel.events.test {
            viewModel.onCancel()
            assertThat(awaitItem()).isEqualTo(EditUiEvent.NavigateBack)
        }

        assertThat(repository.getAllActive()).hasSize(0)
    }
}
