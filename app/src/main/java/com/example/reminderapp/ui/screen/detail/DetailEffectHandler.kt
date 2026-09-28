package com.example.reminderapp.ui.screen.detail

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.reminderapp.core.ext.collectWithLifecycle
import com.example.reminderapp.ui.navigation.ReminderRoute
import kotlinx.coroutines.flow.Flow

@Composable
fun DetailEffectHandler(
    effectFlow: Flow<DetailUiEffect>,
    controller: NavHostController
) {
    effectFlow.collectWithLifecycle { effect ->
        when(effect) {
            DetailUiEffect.NavigateBack -> {
                controller.navigateUp()
            }

            is DetailUiEffect.NavigateToEdit -> {
                controller.navigate(ReminderRoute.Edit(effect.reminderId))
            }
        }
    }
}