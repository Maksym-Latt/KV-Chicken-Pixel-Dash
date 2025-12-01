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

const val PLAYER_WIDTH = 70f
const val PLAYER_HEIGHT = 80f
const val PLAYER_X = 52f
private const val GRAVITY = -1350f
private const val JUMP_FORCE = 520f
private const val HIGH_JUMP_FORCE = 820f
private const val BASE_SPEED = 260f
private const val SPEED_GROWTH = 8f
private const val MAX_SPEED = 520f
private const val TIME_SCORE_RATE = 12f
private const val EGG_SCORE_VALUE = 10
const val ROCK_WIDTH = 64f
const val ROCK_HEIGHT = 44f
const val BOX_WIDTH = 78f
const val BOX_HEIGHT = 120f
const val EGG_WIDTH = 40f
const val EGG_HEIGHT = 48f
private const val DEFAULT_GROUND_HEIGHT = 140f

enum class GameStatus { Ready, Running, Paused, Over }

enum class EntityType { Rock, Box, Egg }

data class Entity(
    val id: Int,
    val type: EntityType,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)

data class GameUiState(
    val score: Int = 0,
    val eggsCollected: Int = 0,
    val bestScore: Int = 0,
    val status: GameStatus = GameStatus.Ready,
    val skin: Skin = SkinCatalog.allSkins.first(),
    val playerY: Float = 0f,
    val playerFrame: Int = 0,
    val speed: Float = BASE_SPEED,
    val groundHeight: Float = DEFAULT_GROUND_HEIGHT,
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
        playerRepository.selectedSkin
    ) { state, best, skinId ->
        state.copy(
            bestScore = max(best, state.bestScore),
            skin = SkinCatalog.findById(skinId)
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, GameUiState())

    private var gameJob: Job? = null
    private var viewportWidth = 360f
    private var viewportHeight = 640f
    private var velocityY = 0f
    private var elapsedTime = 0f
    private var animationTimer = 0f
    private var spawnTimer = 0f
    private var eggSpawnTimer = 0.7f

    init {
        viewModelScope.launch {
            applyAudioVolumes()
            audioController.playGameMusic()
            resetGame()
        }
    }

    fun onViewportChanged(width: Float, height: Float) {
        viewportWidth = width
        viewportHeight = height
    }

    fun jump(high: Boolean) {
        val state = _uiState.value
        if (state.status != GameStatus.Running) return
        val onGround = state.playerY <= 0f
        if (!onGround) return

        velocityY = if (high) HIGH_JUMP_FORCE else JUMP_FORCE
        audioController.playChickenJump()
    }

    fun togglePause() {
        val state = _uiState.value
        if (state.status == GameStatus.Running) {
            _uiState.value = state.copy(status = GameStatus.Paused)
            audioController.pauseMusic()
        } else if (state.status == GameStatus.Paused) {
            _uiState.value = state.copy(status = GameStatus.Running)
            audioController.resumeMusic()
        }
    }

    fun restart() {
        viewModelScope.launch {
            audioController.playGameMusic()
            resetGame()
        }
    }

    fun onExit() {
        gameJob?.cancel()
        audioController.stopMusic()
    }

    private fun resetGame() {
        gameJob?.cancel()
        velocityY = 0f
        elapsedTime = 0f
        animationTimer = 0f
        spawnTimer = 0.2f
        eggSpawnTimer = 0.6f
        _uiState.value = GameUiState(
            status = GameStatus.Running,
            groundHeight = _uiState.value.groundHeight
        )
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
        if (newY < 0f) {
            newY = 0f
            newVelocity = 0f
        }

        val movedObstacles = state.obstacles.mapNotNull { entity ->
            val nextX = entity.x - speed * dt
            if (nextX + entity.width < 0f) null else entity.copy(x = nextX)
        }
        val movedEggs = state.eggs.mapNotNull { entity ->
            val nextX = entity.x - speed * dt
            if (nextX + entity.width < 0f) null else entity.copy(x = nextX)
        }

        val playerRect = Rect(
            left = PLAYER_X,
            top = newY,
            right = PLAYER_X + PLAYER_WIDTH,
            bottom = newY + PLAYER_HEIGHT
        )

        val (survivingEggs, collectedEggs) = movedEggs.partition { !intersects(playerRect, it.toRect()) }

        val collision = movedObstacles.any { intersects(playerRect, it.toRect()) }

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

        if (collision) {
            handleGameOver()
            return
        }

        if (spawnTimer <= 0f) {
            spawnTimer = Random.nextFloat() * 1.3f + (1.1f - (speed / 800f)).coerceAtLeast(0.65f)
            spawnObstacle()
        }

        if (eggSpawnTimer <= 0f) {
            eggSpawnTimer = Random.nextFloat() * 1.3f + 0.8f
            spawnEgg(speed)
        }
    }

    private fun spawnObstacle() {
        val type = if (Random.nextFloat() > 0.45f) EntityType.Rock else EntityType.Box
        val (width, height) = when (type) {
            EntityType.Rock -> ROCK_WIDTH to ROCK_HEIGHT
            EntityType.Box -> BOX_WIDTH to BOX_HEIGHT
            else -> ROCK_WIDTH to ROCK_HEIGHT
        }
        val spawnX = viewportWidth + width + 12f
        val newObstacle = Entity(
            id = Random.nextInt(),
            type = type,
            x = spawnX,
            y = 0f,
            width = width,
            height = height
        )
        _uiState.value = _uiState.value.copy(obstacles = _uiState.value.obstacles + newObstacle)
    }

    private fun spawnEgg(speed: Float) {
        val maxAirRoom = (viewportHeight / 3f).coerceAtLeast(EGG_HEIGHT.toFloat())
        val offsetY = if (speed > 520f) maxAirRoom else maxAirRoom / 2f
        val newEgg = Entity(
            id = Random.nextInt(),
            type = EntityType.Egg,
            x = viewportWidth + EGG_WIDTH + 24f,
            y = offsetY,
            width = EGG_WIDTH,
            height = EGG_HEIGHT
        )
        _uiState.value = _uiState.value.copy(eggs = _uiState.value.eggs + newEgg)
    }

    private fun handleGameOver() {
        val finalState = _uiState.value
        _uiState.value = finalState.copy(status = GameStatus.Over)
        viewModelScope.launch {
            audioController.playChickenHit()
            audioController.pauseMusic()
            playerRepository.updateBestScore(finalState.score)
            if (finalState.eggsCollected > 0) {
                playerRepository.addCoins(finalState.eggsCollected)
            }
        }
    }

    fun resumeAfterPause() {
        val state = _uiState.value
        if (state.status == GameStatus.Paused) {
            _uiState.value = state.copy(status = GameStatus.Running)
            audioController.resumeMusic()
        }
    }

    fun pauseFromLifecycle() {
        val state = _uiState.value
        if (state.status == GameStatus.Running) {
            _uiState.value = state.copy(status = GameStatus.Paused)
            audioController.pauseMusic()
        }
    }

    private suspend fun applyAudioVolumes() {
        audioController.setMusicVolume(if (settingsRepository.musicEnabled.first()) 100 else 0)
        audioController.setSoundVolume(if (settingsRepository.soundEnabled.first()) 100 else 0)
    }
}

private data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

private fun Entity.toRect(): Rect = Rect(x, y, x + width, y + height)

private fun intersects(a: Rect, b: Rect): Boolean {
    val horizontal = a.left < b.right && a.right > b.left
    val vertical = a.top < b.bottom && a.bottom > b.top
    return horizontal && vertical
}
