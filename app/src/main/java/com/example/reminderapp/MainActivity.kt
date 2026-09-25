package com.example.reminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.reminderapp.core.audio.AudioController
import com.example.reminderapp.core.notification.NotificationHelper.Companion.EXTRA_REMINDER_ID
import com.example.reminderapp.ui.navigation.ReminderNavGraph
import com.example.reminderapp.ui.theme.ReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var audioController: AudioController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val openReminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1L)
            .takeIf { it != -1L }
        if (openReminderId != null) {
            audioController.stop(this)
        }

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
