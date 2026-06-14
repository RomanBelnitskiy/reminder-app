package com.example.reminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.notification.NotificationHelper.Companion.EXTRA_REMINDER_ID
import com.example.reminderapp.ui.navigation.ReminderNavGraph
import com.example.reminderapp.ui.theme.ReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val openReminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
            .takeIf { it != -1L }

        setContent {
            ReminderAppTheme {
                val navController = rememberNavController()
                ReminderNavGraph(
                    navController = navController,
                    openReminderId = openReminderId
                )
            }
        }
    }
}
