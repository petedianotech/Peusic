package com.petediano.peusic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.petediano.peusic.data.preferences.UserPreferences
import com.petediano.peusic.ui.theme.PeusicThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UserPreferences(application)

    val themeMode: StateFlow<PeusicThemeMode> = prefs.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PeusicThemeMode.SYSTEM)
    val gapless: StateFlow<Boolean> = prefs.gapless
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val resumeOnStart: StateFlow<Boolean> = prefs.resumeOnStart
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val skipSilence: StateFlow<Boolean> = prefs.skipSilence
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setThemeMode(mode: PeusicThemeMode) { viewModelScope.launch { prefs.setThemeMode(mode) } }
    fun setGapless(enabled: Boolean) { viewModelScope.launch { prefs.setGapless(enabled) } }
    fun setResumeOnStart(enabled: Boolean) { viewModelScope.launch { prefs.setResumeOnStart(enabled) } }
    fun setSkipSilence(enabled: Boolean) { viewModelScope.launch { prefs.setSkipSilence(enabled) } }

    val appVersion: String = try {
        application.packageManager.getPackageInfo(application.packageName, 0).versionName ?: "1.0.0"
    } catch (_: Exception) { "1.0.0" }
}
