package com.petediano.peusic.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.petediano.peusic.ui.theme.PeusicThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "peusic_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val GAPLESS = booleanPreferencesKey("gapless")
        val RESUME_ON_START = booleanPreferencesKey("resume_on_start")
        val SKIP_SILENCE = booleanPreferencesKey("skip_silence")
    }

    val themeMode: Flow<PeusicThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "LIGHT" -> PeusicThemeMode.LIGHT
            "DARK" -> PeusicThemeMode.DARK
            else -> PeusicThemeMode.SYSTEM
        }
    }

    val gapless: Flow<Boolean> = context.dataStore.data.map { it[Keys.GAPLESS] ?: true }
    val resumeOnStart: Flow<Boolean> = context.dataStore.data.map { it[Keys.RESUME_ON_START] ?: true }
    val skipSilence: Flow<Boolean> = context.dataStore.data.map { it[Keys.SKIP_SILENCE] ?: false }

    suspend fun setThemeMode(mode: PeusicThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setGapless(enabled: Boolean) {
        context.dataStore.edit { it[Keys.GAPLESS] = enabled }
    }

    suspend fun setResumeOnStart(enabled: Boolean) {
        context.dataStore.edit { it[Keys.RESUME_ON_START] = enabled }
    }

    suspend fun setSkipSilence(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKIP_SILENCE] = enabled }
    }
}
