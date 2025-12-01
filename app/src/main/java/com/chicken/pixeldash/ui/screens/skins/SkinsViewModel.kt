package com.chicken.pixeldash.ui.screens.skins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.pixeldash.data.player.PlayerRepository
import com.chicken.pixeldash.domain.model.Skin
import com.chicken.pixeldash.domain.model.SkinCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val coins: Int = 0
)

@HiltViewModel
class SkinsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val uiState = combine(
        playerRepository.ownedSkins,
        playerRepository.selectedSkin,
        playerRepository.coinBalance
    ) { owned, selected, coins ->
        SkinsUiState(
            skins = SkinCatalog.allSkins.map { skin ->
                SkinUiModel(
                    skin = skin,
                    owned = owned.contains(skin.id),
                    selected = selected == skin.id
                )
            },
            coins = coins
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SkinsUiState())

    fun onChooseSkin(skin: SkinUiModel) {
        viewModelScope.launch {
            if (!skin.owned && uiState.value.coins >= skin.skin.price) {
                playerRepository.addCoins(-skin.skin.price)
                playerRepository.purchaseSkin(skin.skin.id)
            }
            if (skin.owned || uiState.value.coins >= skin.skin.price) {
                playerRepository.selectSkin(skin.skin.id)
            }
        }
    }
}
