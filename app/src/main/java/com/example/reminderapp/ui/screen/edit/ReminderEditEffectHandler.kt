package com.example.reminderapp.ui.screen.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.Flow

@Composable
fun ReminderEditEffectHandler(
    effects: Flow<ReminderEditUiEffect>,
    controller: NavHostController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleAwareFlow = remember(effects, lifecycleOwner) {
        effects.flowWithLifecycle(
            lifecycle = lifecycleOwner.lifecycle
        )
    }

    LaunchedEffect(Unit) {
        lifecycleAwareFlow.collect { effect ->
            when (effect) {
                ReminderEditUiEffect.NavigateBack -> {
                    controller.navigateUp()
                }
            }
        }
    }
}