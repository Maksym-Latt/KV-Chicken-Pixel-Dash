package com.chicken.pixeldash.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicken.pixeldash.audio.AudioController
import com.chicken.pixeldash.data.player.PlayerRepository
import com.chicken.pixeldash.data.settings.SettingsRepository
import com.chicken.pixeldash.domain.model.Skin
import com.chicken.pixeldash.domain.model.SkinCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.max
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val audioController: AudioController
) : ViewModel() {


}
