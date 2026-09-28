package com.example.reminderapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reminderapp.ui.screen.detail.DetailScreenRoute
import com.example.reminderapp.ui.screen.detail.DetailViewModel
import com.example.reminderapp.ui.screen.edit.EditScreenRoute
import com.example.reminderapp.ui.screen.edit.EditViewModel
import com.example.reminderapp.ui.screen.list.ReminderListScreen
import com.example.reminderapp.ui.screen.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable object ReminderList
@Serializable data class ReminderDetail(val id: Long, val openedFromNotification: Boolean = false)
@Serializable data class ReminderEdit(val id: Long? = null)
@Serializable object Settings

@Composable
fun ReminderNavGraph(
    navController: NavHostController,
    openReminderId: Long? = null
) {
    var handledReminderId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = ReminderList
    ) {
        composable<ReminderList> {
            LaunchedEffect(openReminderId) {
                openReminderId?.let { reminderId ->
                    if (handledReminderId != reminderId) {
                        handledReminderId = reminderId
                        navController.navigate(ReminderDetail(reminderId, true))
                    }
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
            val viewModel: DetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            DetailScreenRoute(
                state = uiState,
                processUiEvent = viewModel::processUiEvent,
                effectFlow = viewModel.effects,
                controller = navController
            )
        }

        composable<ReminderEdit> {
            val viewModel: EditViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            EditScreenRoute(
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
