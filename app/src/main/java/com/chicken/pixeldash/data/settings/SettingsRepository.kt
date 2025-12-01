package com.chicken.pixeldash.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val musicEnabled: Flow<Boolean>
    val soundEnabled: Flow<Boolean>

    fun getMusicVolume(): Int
    fun getSoundVolume(): Int

    suspend fun setMusicEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
}
