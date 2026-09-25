package com.example.reminderapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reminderapp.ui.screen.detail.ReminderDetailScreen
import com.example.reminderapp.ui.screen.edit.ReminderEditScreenRoute
import com.example.reminderapp.ui.screen.edit.ReminderEditViewModel
import com.example.reminderapp.ui.screen.list.ReminderListScreen
import com.example.reminderapp.ui.screen.settings.SettingsScreen
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
                onNavigateBack = {
                    navController.navigate(ReminderList)
                },
                onNavigateToEdit = { id ->
                    navController.navigate(ReminderEdit(id))
                }
            )
        }

        composable<ReminderEdit> {
            val viewModel: ReminderEditViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ReminderEditScreenRoute(
                state = uiState,
                processUiEvent = viewModel::processUiEvent,
                effects = viewModel.effects,
                controller = navController
            )
        }

        composable<Settings> {
            SettingsScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
