package com.example.reminderapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reminderapp.ui.detail.ReminderDetailScreen
import com.example.reminderapp.ui.edit.ReminderEditScreen
import com.example.reminderapp.ui.list.ReminderListScreen
import com.example.reminderapp.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable object ReminderList
@Serializable data class ReminderDetail(val id: Long)
@Serializable data class ReminderEdit(val id: Long? = null)
@Serializable object Settings

@Composable
fun ReminderNavGraph(
    navController: NavHostController,
    openReminderId: Long? = null
) {
    NavHost(
        navController = navController,
        startDestination = ReminderList
    ) {
        composable<ReminderList> {
            LaunchedEffect(openReminderId) {
                openReminderId?.let {
                    navController.navigate(ReminderDetail(it))
                }
            }
            ReminderListScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(ReminderDetail(id))
                },
                onNavigateToCreate = {
                    navController.navigate(ReminderEdit())
                },
                onNavigateToSettings = {
                    navController.navigate(Settings)
                }
            )
        }

        composable<ReminderDetail> {
            ReminderDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = { id ->
                    navController.navigate(ReminderEdit(id))
                }
            )
        }

        composable<ReminderEdit> {
            ReminderEditScreen(onNavigateBack = { navController.navigateUp() })
        }

        composable<Settings> {
            SettingsScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
