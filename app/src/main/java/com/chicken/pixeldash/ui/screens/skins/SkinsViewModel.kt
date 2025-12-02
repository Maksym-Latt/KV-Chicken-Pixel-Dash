package com.chicken.pixeldash.ui.screens.skins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.pixeldash.data.player.PlayerRepository
import com.chicken.pixeldash.domain.model.Skin
import com.chicken.pixeldash.domain.model.SkinCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SkinUiModel(
    val skin: Skin,
    val owned: Boolean,
    val selected: Boolean
)

data class SkinsUiState(
    val skins: List<SkinUiModel> = emptyList(),
    val coins: Int = 0,
    val currentIndex: Int = 0
) {
    val currentSkin: SkinUiModel?
        get() = skins.getOrNull(currentIndex)
}

@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    private val indexFlow = MutableStateFlow(0)

    val uiState = combine(
        playerRepository.ownedSkins,
        playerRepository.selectedSkin,
        playerRepository.coinBalance,
        indexFlow
    ) { owned, selected, coins, index ->

        val skins = SkinCatalog.allSkins.map { skin ->
            SkinUiModel(
                skin = skin,
                owned = owned.contains(skin.id),
                selected = selected == skin.id
            )
        }

        SkinsUiState(
            skins = skins,
            coins = coins,
            currentIndex = index.coerceIn(0, skins.lastIndex)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SkinsUiState())

    fun nextSkin() {
        val max = uiState.value.skins.lastIndex
        val next = (uiState.value.currentIndex + 1).coerceAtMost(max)
        indexFlow.value = next
    }

    fun prevSkin() {
        val prev = (uiState.value.currentIndex - 1).coerceAtLeast(0)
        indexFlow.value = prev
    }

    fun onChooseSkin(skin: SkinUiModel) {
        viewModelScope.launch {
            val coins = uiState.value.coins

            if (!skin.owned && coins >= skin.skin.price) {
                playerRepository.addCoins(-skin.skin.price)
                playerRepository.purchaseSkin(skin.skin.id)
            }

            if (skin.owned || coins >= skin.skin.price) {
                playerRepository.selectSkin(skin.skin.id)
            }
        }
    }
}
