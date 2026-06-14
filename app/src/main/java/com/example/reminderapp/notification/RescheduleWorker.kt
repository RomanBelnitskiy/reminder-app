package com.example.reminderapp.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.reminderapp.data.repository.ReminderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        repository.getAllActive()
            .filter { it.reminderDateTime > now }
            .forEach { scheduler.schedule(it) }
        return Result.success()
    }
}
