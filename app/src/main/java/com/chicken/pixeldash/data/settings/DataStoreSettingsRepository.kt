package com.chicken.pixeldash.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val musicKey = booleanPreferencesKey("music_enabled")
    private val soundKey = booleanPreferencesKey("sound_enabled")
    private val musicVolumeKey = intPreferencesKey("music_volume")
    private val soundVolumeKey = intPreferencesKey("sound_volume")

    override val musicEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[musicKey] ?: true
    }

    override val soundEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[soundKey] ?: true
    }

    override val musicVolumeFlow: Flow<Int> =
        context.settingsDataStore.data
            .map { it[musicVolumeKey] ?: DEFAULT_VOLUME }

    override val soundVolumeFlow: Flow<Int> =
        context.settingsDataStore.data
            .map { it[soundVolumeKey] ?: DEFAULT_VOLUME }

    override suspend fun setMusicEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[musicKey] = enabled
            prefs[musicVolumeKey] = if (enabled) DEFAULT_VOLUME else 0
        }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[soundKey] = enabled
            prefs[soundVolumeKey] = if (enabled) DEFAULT_VOLUME else 0
        }
    }

    companion object {
        private const val DEFAULT_VOLUME = 100
    }
}
