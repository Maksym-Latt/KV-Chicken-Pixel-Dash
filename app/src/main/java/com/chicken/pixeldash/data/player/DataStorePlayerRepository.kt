package com.chicken.pixeldash.data.player

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.playerDataStore: DataStore<Preferences> by preferencesDataStore(name = "player")

@Singleton
class DataStorePlayerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private val coinsKey = intPreferencesKey("coins")
    private val bestScoreKey = intPreferencesKey("best_score")
    private val selectedSkinKey = stringPreferencesKey("selected_skin")
    private val ownedSkinsKey = stringSetPreferencesKey("owned_skins")

    override val coinBalance: Flow<Int> = context.playerDataStore.data.map { prefs ->
        prefs[coinsKey] ?: DEFAULT_COINS
    }

    override val bestScore: Flow<Int> = context.playerDataStore.data.map { prefs ->
        prefs[bestScoreKey] ?: 0
    }

    override val selectedSkin: Flow<String> = context.playerDataStore.data.map { prefs ->
        prefs[selectedSkinKey] ?: DEFAULT_SKIN
    }

    override val ownedSkins: Flow<Set<String>> = context.playerDataStore.data.map { prefs ->
        prefs[ownedSkinsKey] ?: setOf(DEFAULT_SKIN)
    }

    override suspend fun addCoins(amount: Int) {
        if (amount == 0) return
        context.playerDataStore.edit { prefs ->
            val current = prefs[coinsKey] ?: DEFAULT_COINS
            prefs[coinsKey] = (current + amount).coerceAtLeast(0)
        }
    }

    override suspend fun updateBestScore(score: Int) {
        context.playerDataStore.edit { prefs ->
            val current = prefs[bestScoreKey] ?: 0
            if (score > current) {
                prefs[bestScoreKey] = score
            }
        }
    }

    override suspend fun selectSkin(skinId: String) {
        context.playerDataStore.edit { prefs ->
            prefs[selectedSkinKey] = skinId
            val owned = prefs[ownedSkinsKey] ?: mutableSetOf(DEFAULT_SKIN)
            prefs[ownedSkinsKey] = owned + skinId
        }
    }

    override suspend fun purchaseSkin(skinId: String) {
        context.playerDataStore.edit { prefs ->
            val owned = prefs[ownedSkinsKey] ?: mutableSetOf(DEFAULT_SKIN)
            prefs[ownedSkinsKey] = owned + skinId
        }
    }

    companion object {
        const val DEFAULT_SKIN = "classic"
        const val DEFAULT_COINS = 50
    }
}
