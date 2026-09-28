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
import com.example.reminderapp.ui.screen.list.ListScreenRoute
import com.example.reminderapp.ui.screen.list.ListViewModel
import com.example.reminderapp.ui.screen.settings.SettingsScreen

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
        startDestination = ReminderRoute.List
    ) {
        composable<ReminderRoute.List> {
            val viewModel: ListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val reminders by viewModel.reminders.collectAsStateWithLifecycle()

            LaunchedEffect(openReminderId) {
                openReminderId?.let { reminderId ->
                    if (handledReminderId != reminderId) {
                        handledReminderId = reminderId
                        navController.navigate(ReminderRoute.Detail(reminderId, true))
                    }
                }
            }

            ListScreenRoute(
                state = uiState,
                reminders = reminders,
                processUiEvent = viewModel::processUiEvent,
                effectFlow = viewModel.effects,
                controller = navController
            )
        }

        composable< ReminderRoute.Detail> {
            val viewModel: DetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            DetailScreenRoute(
                state = uiState,
                processUiEvent = viewModel::processUiEvent,
                effectFlow = viewModel.effects,
                controller = navController
            )
        }

        composable< ReminderRoute.Edit> {
            val viewModel: EditViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            EditScreenRoute(
                state = uiState,
                processUiEvent = viewModel::processUiEvent,
                effects = viewModel.effects,
                controller = navController
            )
        }

        composable< ReminderRoute.Settings> {
            SettingsScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}
