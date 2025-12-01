package com.chicken.pixeldash.data.player

import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    val coinBalance: Flow<Int>
    val bestScore: Flow<Int>
    val selectedSkin: Flow<String>
    val ownedSkins: Flow<Set<String>>

    suspend fun addCoins(amount: Int)
    suspend fun updateBestScore(score: Int)
    suspend fun selectSkin(skinId: String)
    suspend fun purchaseSkin(skinId: String)
}
