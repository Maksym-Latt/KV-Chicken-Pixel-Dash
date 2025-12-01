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

private const val DEFAULT_SIZE_SCALE = 1f
private const val DEFAULT_COLLIDER_SCALE = 0.8f

private const val PLAYER_BASE_WIDTH = 70f
private const val PLAYER_BASE_HEIGHT = 80f
private const val PLAYER_SIZE_SCALE = DEFAULT_SIZE_SCALE
const val PLAYER_WIDTH = PLAYER_BASE_WIDTH * PLAYER_SIZE_SCALE
const val PLAYER_HEIGHT = PLAYER_BASE_HEIGHT * PLAYER_SIZE_SCALE
const val PLAYER_X = 52f
private const val PLAYER_COLLIDER_SCALE = DEFAULT_COLLIDER_SCALE

private const val GRAVITY = -1350f
private const val JUMP_FORCE = 870f
private const val HIGH_JUMP_FORCE = 1120f
private const val BASE_SPEED = 260f
private const val SPEED_GROWTH = 8f
private const val MAX_SPEED = 520f
private const val TIME_SCORE_RATE = 12f
private const val EGG_SCORE_VALUE = 10

private const val ROCK_BASE_WIDTH = 64f
private const val ROCK_BASE_HEIGHT = 44f
private const val ROCK_SIZE_SCALE = DEFAULT_SIZE_SCALE
private const val ROCK_COLLIDER_SCALE = DEFAULT_COLLIDER_SCALE
const val ROCK_WIDTH = ROCK_BASE_WIDTH * ROCK_SIZE_SCALE
const val ROCK_HEIGHT = ROCK_BASE_HEIGHT * ROCK_SIZE_SCALE

private const val BOX_BASE_WIDTH = 78f
private const val BOX_BASE_HEIGHT = 120f
private const val BOX_SIZE_SCALE = DEFAULT_SIZE_SCALE
private const val BOX_COLLIDER_SCALE = DEFAULT_COLLIDER_SCALE
const val BOX_WIDTH = BOX_BASE_WIDTH * BOX_SIZE_SCALE
const val BOX_HEIGHT = BOX_BASE_HEIGHT * BOX_SIZE_SCALE

private const val EGG_SIZE_SCALE = DEFAULT_SIZE_SCALE
private const val EGG_COLLIDER_SCALE = DEFAULT_COLLIDER_SCALE
const val EGG_WIDTH = 40f
const val EGG_HEIGHT = 48f

private const val DEFAULT_GROUND_HEIGHT = 140f

enum class GameStatus { Ready, Running, Paused, Over }

enum class EntityType { Rock, Box, Egg }

private data class EntityTemplate(
    val width: Float,
    val height: Float,
    val sizeScale: Float,
    val colliderScale: Float
)

data class Entity(
    val id: Int,
    val type: EntityType,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val sizeScale: Float = DEFAULT_SIZE_SCALE,
    val colliderScale: Float = DEFAULT_COLLIDER_SCALE
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
            val widthWithScale = entity.width * entity.sizeScale
            if (nextX + widthWithScale < 0f) null else entity.copy(x = nextX)
        }
        val movedEggs = state.eggs.mapNotNull { entity ->
            val nextX = entity.x - speed * dt
            val widthWithScale = entity.width * entity.sizeScale
            if (nextX + widthWithScale < 0f) null else entity.copy(x = nextX)
        }

        val playerRect = scaledRect(
            x = PLAYER_X,
            y = newY,
            width = PLAYER_WIDTH,
            height = PLAYER_HEIGHT,
            colliderScale = PLAYER_COLLIDER_SCALE
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
        val template = when (type) {
            EntityType.Rock -> EntityTemplate(ROCK_BASE_WIDTH, ROCK_BASE_HEIGHT, ROCK_SIZE_SCALE, ROCK_COLLIDER_SCALE)
            EntityType.Box -> EntityTemplate(BOX_BASE_WIDTH, BOX_BASE_HEIGHT, BOX_SIZE_SCALE, BOX_COLLIDER_SCALE)
            else -> EntityTemplate(ROCK_BASE_WIDTH, ROCK_BASE_HEIGHT, ROCK_SIZE_SCALE, ROCK_COLLIDER_SCALE)
        }
        val spawnX = viewportWidth + (template.width * template.sizeScale) + 12f
        val newObstacle = Entity(
            id = Random.nextInt(),
            type = type,
            x = spawnX,
            y = 0f,
            width = template.width,
            height = template.height,
            sizeScale = template.sizeScale,
            colliderScale = template.colliderScale
        )
        _uiState.value = _uiState.value.copy(obstacles = _uiState.value.obstacles + newObstacle)
    }

    private fun spawnEgg(speed: Float) {
        val maxAirRoom = (viewportHeight / 3f).coerceAtLeast(EGG_HEIGHT * EGG_SIZE_SCALE)
        val offsetY = if (speed > 520f) maxAirRoom else maxAirRoom / 2f
        val scaledEggWidth = EGG_WIDTH * EGG_SIZE_SCALE
        val newEgg = Entity(
            id = Random.nextInt(),
            type = EntityType.Egg,
            x = viewportWidth + scaledEggWidth + 24f,
            y = offsetY,
            width = EGG_WIDTH,
            height = EGG_HEIGHT,
            sizeScale = EGG_SIZE_SCALE,
            colliderScale = EGG_COLLIDER_SCALE
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

private fun Entity.toRect(): Rect = scaledRect(x, y, width * sizeScale, height * sizeScale, colliderScale)

private fun scaledRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    colliderScale: Float
): Rect {
    val scaledWidth = width * colliderScale
    val scaledHeight = height * colliderScale
    val offsetX = (width - scaledWidth) / 2f
    val offsetY = (height - scaledHeight) / 2f
    return Rect(
        left = x + offsetX,
        top = y + offsetY,
        right = x + offsetX + scaledWidth,
        bottom = y + offsetY + scaledHeight
    )
}

private fun intersects(a: Rect, b: Rect): Boolean {
    val horizontal = a.left < b.right && a.right > b.left
    val vertical = a.top < b.bottom && a.bottom > b.top
    return horizontal && vertical
}
