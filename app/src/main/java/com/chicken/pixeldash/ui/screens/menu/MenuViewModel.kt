package com.chicken.pixeldash.ui.screens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.pixeldash.audio.AudioController
import com.chicken.pixeldash.data.player.PlayerRepository
import com.chicken.pixeldash.data.settings.SettingsRepository
import com.chicken.pixeldash.domain.model.SkinCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MenuUiState(
    val bestScore: Int = 0,
    val coins: Int = 0,
    val musicEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val selectedSkinName: String = SkinCatalog.allSkins.first().name
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val audioController: AudioController
) : ViewModel() {

    val uiState = combine(
        playerRepository.bestScore,
        playerRepository.coinBalance,
        settingsRepository.musicEnabled,
        settingsRepository.soundEnabled,
        playerRepository.selectedSkin
    ) { best, coins, music, sound, skinId ->
        val skin = SkinCatalog.findById(skinId)
        MenuUiState(
            bestScore = best,
            coins = coins,
            musicEnabled = music,
            soundEnabled = sound,
            selectedSkinName = skin.name
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MenuUiState())

    init {
        viewModelScope.launch {
            applyAudioVolumes()
            audioController.playMenuMusic()
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            val enabled = uiState.value.musicEnabled
            settingsRepository.setMusicEnabled(!enabled)
            applyAudioVolumes()
            if (!enabled) audioController.playMenuMusic() else audioController.pauseMusic()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            val enabled = uiState.value.soundEnabled
            settingsRepository.setSoundEnabled(!enabled)
            applyAudioVolumes()
        }
    }

    private suspend fun applyAudioVolumes() {
        audioController.setMusicVolume(if (settingsRepository.musicEnabled.first()) 100 else 0)
        audioController.setSoundVolume(if (settingsRepository.soundEnabled.first()) 100 else 0)
    }
}
