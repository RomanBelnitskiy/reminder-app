package com.example.reminderapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val soundPrefs: PreferencesRepository
) : ViewModel() {

    val soundEnabled = soundPrefs.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun onSoundToggle(enabled: Boolean) {
        viewModelScope.launch { soundPrefs.setSoundEnabled(enabled) }
    }
}
