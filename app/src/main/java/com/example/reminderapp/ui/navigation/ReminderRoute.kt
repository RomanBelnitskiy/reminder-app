package com.example.reminderapp.ui.navigation

import kotlinx.serialization.Serializable

sealed interface ReminderRoute {
    @Serializable object List : ReminderRoute
    @Serializable data class Detail(val id: Long, val openedFromNotification: Boolean = false) : ReminderRoute
    @Serializable data class Edit(val id: Long? = null) : ReminderRoute
    @Serializable object Settings : ReminderRoute
}
