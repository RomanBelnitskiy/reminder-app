package com.example.reminderapp.ui.screen.edit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.reminderapp.core.ext.collectWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun ReminderEditEffectHandler(
    effects: Flow<ReminderEditUiEffect>,
    controller: NavHostController
) {
    effects.collectWithLifecycle { effect ->
        when (effect) {
            ReminderEditUiEffect.NavigateBack -> {
                controller.navigateUp()
            }
        }
    }
}