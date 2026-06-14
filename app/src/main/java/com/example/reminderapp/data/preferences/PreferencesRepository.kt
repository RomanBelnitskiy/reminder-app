package com.example.reminderapp.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        private val FIRST_NOTIFICATION = booleanPreferencesKey("is_first_notification")
    }

    val soundEnabled: Flow<Boolean> = dataStore.data.map { it[SOUND_ENABLED] ?: true }
    val isFirstNotification: Flow<Boolean> = dataStore.data.map { it[FIRST_NOTIFICATION] ?: true }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun setFirstNotification(isFirst: Boolean) {
        dataStore.edit { it[FIRST_NOTIFICATION] = isFirst }
    }
}
