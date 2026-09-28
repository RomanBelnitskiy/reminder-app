package com.example.reminderapp.ui.screen.list

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.reminderapp.core.ext.collectWithLifecycle
import com.example.reminderapp.ui.navigation.ReminderRoute
import kotlinx.coroutines.flow.Flow

@Composable
fun ListEffectHandler(
    effectFlow: Flow<ListUiEffect>,
    controller: NavHostController
) {
    effectFlow.collectWithLifecycle { effect ->
        when(effect) {
            is ListUiEffect.NavigateToDetail -> {
                controller.navigate(ReminderRoute.Detail(id = effect.reminderId))
            }

            ListUiEffect.NavigateToCreate -> {
                controller.navigate(ReminderRoute.Edit())
            }

            ListUiEffect.NavigateToSettings -> {
                controller.navigate(ReminderRoute.Settings)
            }
        }
    }
}