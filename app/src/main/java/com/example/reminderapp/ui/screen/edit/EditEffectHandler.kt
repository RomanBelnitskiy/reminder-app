package com.example.reminderapp.ui.screen.edit

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.reminderapp.core.ext.collectWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun EditEffectHandler(
    effects: Flow<EditUiEffect>,
    controller: NavHostController
) {
    effects.collectWithLifecycle { effect ->
        when (effect) {
            EditUiEffect.NavigateBack -> {
                controller.navigateUp()
            }
        }
    }
}