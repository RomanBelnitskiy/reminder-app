package com.example.reminderapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.reminderapp.MainActivity
import com.example.reminderapp.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun fabNavigatesToCreateScreen() {
        composeRule
            .onNodeWithContentDescription(
                composeRule.activity.getString(R.string.fab_create_reminder)
            )
            .performClick()

        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.screen_title_create_reminder))
            .assertIsDisplayed()
    }

    @Test
    fun settingsIconNavigatesToSettingsScreen() {
        composeRule
            .onNodeWithContentDescription(
                composeRule.activity.getString(R.string.action_settings)
            )
            .performClick()

        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.screen_title_settings))
            .assertIsDisplayed()
    }
}
