package com.chicken.pixeldash.ui.screens.game

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
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val PLAYER_X = 32f
private const val GRAVITY = -3000f
private const val JUMP_FORCE = 900f
private const val HIGH_JUMP_FORCE = 1300f
private const val BASE_SPEED = 260f
private const val SPEED_GROWTH = 8f
private const val MAX_SPEED = 520f
private const val TIME_SCORE_RATE = 12f
private const val EGG_SCORE_VALUE = 10
private const val DEFAULT_GROUND_HEIGHT = 140f

enum class GameStatus { Ready, Running, Paused, Over }

enum class EntityType { Rock, Box, Egg }

data class Entity(
    val id: Int,
    val type: EntityType,
    val x: Float,
    val y: Float,
    val sizeScale: Float = 1f,
    val hitboxScale: Float = 1f
)

data class GameUiState(
    val score: Int = 0,
    val eggsCollected: Int = 0,
    val bestScore: Int = 0,
    val status: GameStatus = GameStatus.Ready,
    val skin: Skin = SkinCatalog.allSkins.first(),
    val musicEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val playerY: Float = 0f,
    val playerFrame: Int = 0,
    val speed: Float = BASE_SPEED,
    val groundHeight: Float = DEFAULT_GROUND_HEIGHT,
    val playerSizeScale: Float = 1f,
    val playerHitboxScale: Float = 1f,
    val obstacles: List<Entity> = emptyList(),
    val eggs: List<Entity> = emptyList(),
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val audioController: AudioController
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = combine(
        _uiState,
        playerRepository.bestScore,
        playerRepository.selectedSkin,
        settingsRepository.musicEnabled,
        settingsRepository.soundEnabled
    ) { state, best, skinId, musicEnabled, soundEnabled ->
        state.copy(
            bestScore = max(best, state.bestScore),
            skin = SkinCatalog.findById(skinId),
            musicEnabled = musicEnabled,
            soundEnabled = soundEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GameUiState())

    private var gameJob: Job? = null
    private var viewportWidth = 360f
    private var viewportHeight = 640f
    private var velocityY = 0f
    private var elapsedTime = 0f
    private var animationTimer = 0f
    private var spawnTimer = 0f
    private var eggSpawnTimer = 0.5f

    private var hitPlayed = false
    private var gameOverTriggered = false

    init {
        viewModelScope.launch { applyAudioVolumes() }
    }

    fun onViewportChanged(width: Float, height: Float) {
        viewportWidth = width
        viewportHeight = height
    }

    fun updateGroundHeight(height: Float) {
        if (height != _uiState.value.groundHeight) {
            _uiState.value = _uiState.value.copy(groundHeight = height)
        }
    }

    fun jump(high: Boolean) {
        val state = _uiState.value
        if (state.status != GameStatus.Running) return
        val onGround = state.playerY < 3f
        if (!onGround) return

        velocityY = if (high) HIGH_JUMP_FORCE else JUMP_FORCE
        audioController.playChickenJump()
    }

    fun pauseGame() {
        val state = _uiState.value
        if (state.status == GameStatus.Running) {
            _uiState.value = state.copy(status = GameStatus.Paused)
            audioController.pauseMusic()
        }
    }

    fun resumeGame() {
        val state = _uiState.value
        if (state.status == GameStatus.Paused) {
            _uiState.value = state.copy(status = GameStatus.Running)
            audioController.resumeMusic()
        }
    }

    fun toggleMusic() {
        viewModelScope.launch {
            val enabled = uiState.value.musicEnabled
            settingsRepository.setMusicEnabled(!enabled)
            applyAudioVolumes()
            val state = _uiState.value
            if (enabled) {
                audioController.pauseMusic()
            } else if (state.status == GameStatus.Running) {
                audioController.playGameMusic()
            }
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            val enabled = uiState.value.soundEnabled
            settingsRepository.setSoundEnabled(!enabled)
            applyAudioVolumes()
        }
    }

    fun startNewGame(showIntro: Boolean) {
        viewModelScope.launch {
            applyAudioVolumes()
            if (showIntro) {
                audioController.pauseMusic()
            } else {
                audioController.playGameMusic()
            }
            resetGame(if (showIntro) GameStatus.Ready else GameStatus.Running)
        }
    }

    fun startRun() {
        val state = _uiState.value
        if (state.status == GameStatus.Ready) {
            _uiState.value = state.copy(status = GameStatus.Running)
            audioController.playGameMusic()
        }
    }

    fun restart() {
        startNewGame(showIntro = false)
    }

    fun onExit() {
        gameJob?.cancel()
        audioController.stopMusic()
    }

    private fun resetGame(startStatus: GameStatus) {
        gameJob?.cancel()
        velocityY = 0f
        elapsedTime = 0f
        animationTimer = 0f
        spawnTimer = 0.2f
        eggSpawnTimer = 0.45f
        _uiState.value = GameUiState(
            status = startStatus,
            groundHeight = _uiState.value.groundHeight
        )

        hitPlayed = false
        gameOverTriggered = false

        gameJob = viewModelScope.launch { gameLoop() }
    }

    private suspend fun gameLoop() {
        var last = System.currentTimeMillis()
        while (isActive) {
            val now = System.currentTimeMillis()
            val dt = (now - last).coerceAtMost(32).toFloat() / 1000f
            last = now
            val state = _uiState.value
            if (state.status == GameStatus.Running) {
                tick(dt)
            }
            delay(16)
        }
    }

    private fun tick(dt: Float) {
        elapsedTime += dt
        animationTimer += dt
        spawnTimer -= dt
        eggSpawnTimer -= dt

        var state = _uiState.value
        val speed = (BASE_SPEED + elapsedTime * SPEED_GROWTH).coerceAtMost(MAX_SPEED)

        val newFrame = ((animationTimer * 10).toInt() % 4)
        var newY = state.playerY + velocityY * dt
        var newVelocity = velocityY + GRAVITY * dt
        if (newY <= 0.5f) {
            newY = 0f
            newVelocity = 0f
        }

        val movedObstacles = state.obstacles.mapNotNull { entity ->
            val nextX = entity.x - speed * dt
            if (nextX + entity.spriteWidth() < 0f) null else entity.copy(x = nextX)
        }
        val movedEggs = state.eggs.mapNotNull { entity ->
            val nextX = entity.x - speed * dt
            if (nextX + entity.spriteWidth() < 0f) null else entity.copy(x = nextX)
        }

        val playerRect = playerHitboxRect(newY)


        val (survivingEggs, collectedEggs) = movedEggs.partition { !intersects(playerRect, it.hitboxRect()) }

        if (collectedEggs.isNotEmpty()) {
            audioController.playCollectEgg()
        }

        val collision = movedObstacles.any { intersects(playerRect, it.hitboxRect()) }

        if (collision && !hitPlayed) {
            hitPlayed = true
            audioController.playChickenHit()
        }
        val newScore = (elapsedTime * TIME_SCORE_RATE).toInt() + (state.eggsCollected + collectedEggs.size) * EGG_SCORE_VALUE

        state = state.copy(
            playerY = newY,
            playerFrame = newFrame,
            speed = speed,
            obstacles = movedObstacles,
            eggs = survivingEggs,
            eggsCollected = state.eggsCollected + collectedEggs.size,
            score = newScore
        )

        _uiState.value = state
        velocityY = newVelocity

        if (collision && !gameOverTriggered) {
            gameOverTriggered = true
            viewModelScope.launch {
                delay(120)
                handleGameOver()
            }
            return
        }

        if (spawnTimer <= 0f) {
            spawnTimer = Random.nextFloat() * 1.3f + (1.1f - (speed / 800f)).coerceAtLeast(0.65f)
            spawnObstacle()
        }

        if (eggSpawnTimer <= 0f) {
            eggSpawnTimer = Random.nextFloat() * 1.0f + 0.6f
            spawnEgg()
        }
    }

    private fun spawnObstacle() {
        val type = if (Random.nextFloat() > 0.45f) EntityType.Rock else EntityType.Box
        val (width, _) = Entity(id = 0, type = type, x = 0f, y = 0f).spriteSize()

        val spawnX = viewportWidth + width + 12f

        val newObstacle = Entity(
            id = Random.nextInt(),
            type = type,
            x = spawnX,
            y = 0f
        )

        _uiState.value = _uiState.value.copy(
            obstacles = _uiState.value.obstacles + newObstacle
        )
    }


    private fun spawnEgg() {
        val temp = Entity(id = 0, type = EntityType.Egg, x = 0f, y = 0f)
        val (width, height) = temp.spriteSize()

        val baseSpawnX = viewportWidth + width + 24f

        val closestObstacleFront = _uiState.value.obstacles
            .maxOfOrNull { it.x + it.spriteWidth() }
            ?: 0f

        val closestEggFront = _uiState.value.eggs
            .maxOfOrNull { it.x + it.spriteWidth() }
            ?: 0f

        val minSpacing = 96f
        val spawnX = listOf(baseSpawnX, closestObstacleFront + minSpacing, closestEggFront + minSpacing).max()

        val isGroundEgg = Random.nextFloat() < 0.7f

        val y = if (isGroundEgg) {
            0f
        } else {
            val maxAir = (viewportHeight * 0.15f).coerceAtLeast(height)
            Random.nextFloat() * maxAir
        }

        val newEgg = Entity(
            id = Random.nextInt(),
            type = EntityType.Egg,
            x = spawnX,
            y = y
        )

        _uiState.value = _uiState.value.copy(
            eggs = _uiState.value.eggs + newEgg
        )
    }


    private fun handleGameOver() {
        val finalState = _uiState.value
        _uiState.value = finalState.copy(status = GameStatus.Over)
        viewModelScope.launch {
            audioController.playGameWin()
            audioController.pauseMusic()
            playerRepository.updateBestScore(finalState.score)
            if (finalState.eggsCollected > 0) {
                playerRepository.addCoins(finalState.eggsCollected)
            }
        }
    }

    fun pauseFromLifecycle() {
        val state = _uiState.value
        if (state.status == GameStatus.Running) {
            pauseGame()
        } else {
            audioController.pauseMusic()
        }
    }

    fun handleResumeLifecycle() {
        val state = _uiState.value
        if (state.status == GameStatus.Running) {
            audioController.resumeMusic()
        } else {
            audioController.pauseMusic()
        }
    }

    private suspend fun applyAudioVolumes() {
        audioController.setMusicVolume(if (settingsRepository.musicEnabled.first()) 100 else 0)
        audioController.setSoundVolume(if (settingsRepository.soundEnabled.first()) 100 else 0)
    }
}