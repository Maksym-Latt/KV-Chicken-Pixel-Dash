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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TICK_MS = 16L

sealed class GamePhase { object Playing : GamePhase(); object Paused : GamePhase(); object GameOver : GamePhase() }

data class Obstacle(
    val id: Int,
    val type: ObstacleType,
    var x: Float,
    val width: Float,
    val height: Float
)

data class EggPickup(
    val id: Int,
    var x: Float,
    val y: Float,
    val size: Float = 32f
)

enum class ObstacleType { ROCK, BOX, GAP }

data class GameUiState(
    val skin: Skin = SkinCatalog.allSkins.first(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val runEggs: Int = 0,
    val totalCoins: Int = 0,
    val playerY: Float = 0f,
    val playerVelocity: Float = 0f,
    val groundHeight: Float = 80f,
    val obstacles: List<Obstacle> = emptyList(),
    val eggs: List<EggPickup> = emptyList(),
    val phase: GamePhase = GamePhase.Paused,
    val musicEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val settingsRepository: SettingsRepository,
    private val audioController: AudioController
) : ViewModel() {

    private var tickJob: Job? = null
    private var viewportWidth = 1080f
    private var viewportHeight = 1920f
    private val playerX get() = viewportWidth * 0.2f

    private var obstacleSeed = 0

    private val gameState = MutableStateFlow(GameUiState())
    private var currentState: GameUiState
        get() = gameState.value
        set(value) { gameState.value = value }

    val uiState = combine(
        gameState,
        playerRepository.bestScore,
        playerRepository.coinBalance,
        playerRepository.selectedSkin,
        settingsRepository.musicEnabled,
        settingsRepository.soundEnabled,
    ) { current, best, coins, skinId, music, sound ->
        current.copy(
            bestScore = best,
            totalCoins = coins,
            skin = SkinCatalog.findById(skinId),
            musicEnabled = music,
            soundEnabled = sound
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, gameState.value)

    init {
        viewModelScope.launch {
            applyVolumes()
            audioController.playGameMusic()
            resetRun()
        }
    }

    fun updateViewport(width: Float, height: Float) {
        viewportWidth = width
        viewportHeight = height
    }

    fun onTap() {
        if (currentState.phase == GamePhase.GameOver) return
        jump(strong = false)
    }

    fun onLongTap() {
        if (currentState.phase == GamePhase.GameOver) return
        jump(strong = true)
    }

    fun pause() {
        if (currentState.phase == GamePhase.GameOver) return
        currentState = currentState.copy(phase = GamePhase.Paused)
    }

    fun resume() {
        if (currentState.phase == GamePhase.GameOver) return
        currentState = currentState.copy(phase = GamePhase.Playing)
        ensureTicker()
    }

    fun restart() {
        resetRun()
    }

    private fun resetRun() {
        currentState = currentState.copy(
            score = 0,
            runEggs = 0,
            playerY = 0f,
            playerVelocity = 0f,
            obstacles = emptyList(),
            eggs = emptyList(),
            phase = GamePhase.Playing
        )
        obstacleSeed = 0
        ensureTicker()
        viewModelScope.launch { audioController.playGameMusic() }
    }

    private fun ensureTicker() {
        if (tickJob?.isActive == true) return
        tickJob = viewModelScope.launch {
            var last = System.currentTimeMillis()
            while (true) {
                val now = System.currentTimeMillis()
                val dt = (now - last).coerceAtMost(32).toFloat() / 1000f
                last = now
                update(dt)
                delay(TICK_MS)
            }
        }
    }

    private fun update(dt: Float) {
        val current = currentState
        if (current.phase != GamePhase.Playing) return

        val gravity = 1800f
        val maxJump = viewportHeight * 0.35f
        val groundY = current.groundHeight

        val nextVelocity = current.playerVelocity - gravity * dt
        var nextY = (current.playerY + nextVelocity * dt).coerceAtLeast(-maxJump)
        var landed = false

        if (nextY < 0f) {
            nextY = 0f
            landed = true
        }

        val speed = 320f + (current.score * 0.1f)

        val updatedObstacles = current.obstacles.mapNotNull { obstacle ->
            val newX = obstacle.x - speed * dt
            if (newX + obstacle.width < -48f) null else obstacle.copy(x = newX)
        }

        val updatedEggs = current.eggs.mapNotNull { egg ->
            val newX = egg.x - speed * dt
            if (newX + egg.size < -48f) null else egg.copy(x = newX)
        }

        val (collision, gapFall) = detectCollision(nextY, updatedObstacles)
        val collectResult = collectEggs(updatedEggs, nextY)

        val newScore = current.score + (speed * dt / 10f).toInt()
        val newRunEggs = current.runEggs + collectResult.collected

        if (collectResult.collected > 0 && current.soundEnabled) audioController.playCollectEgg()

        val gameOver = collision || gapFall || nextY < -maxJump

        val finalState = if (gameOver) {
            viewModelScope.launch {
                playerRepository.addCoins(newRunEggs)
                playerRepository.updateBestScore(max(current.bestScore, newScore))
                audioController.playChickenHit()
            }
            current.copy(
                score = newScore,
                runEggs = newRunEggs,
                eggs = collectResult.remaining,
                obstacles = updatedObstacles,
                playerVelocity = 0f,
                playerY = nextY,
                phase = GamePhase.GameOver
            )
        } else {
            val spawnReady = shouldSpawn(updatedObstacles)
            val afterSpawn = if (spawnReady) spawnNext(updatedObstacles, collectResult.remaining) else Pair(updatedObstacles, collectResult.remaining)

            val velocityAfterLand = if (landed) 0f else nextVelocity
            current.copy(
                score = newScore,
                runEggs = newRunEggs,
                playerY = nextY,
                playerVelocity = velocityAfterLand,
                obstacles = afterSpawn.first,
                eggs = afterSpawn.second,
                phase = GamePhase.Playing
            )
        }

        this.currentState = finalState
    }

    private fun detectCollision(playerY: Float, obstacles: List<Obstacle>): Pair<Boolean, Boolean> {
        val playerHeight = 96f
        val playerWidth = 72f
        val playerBottom = playerY + playerHeight

        obstacles.forEach { obstacle ->
            val overlapX = playerX + playerWidth > obstacle.x && playerX < obstacle.x + obstacle.width
            if (!overlapX) return@forEach
            when (obstacle.type) {
                ObstacleType.GAP -> {
                    if (playerY <= 0f) {
                        return true to true
                    }
                }

                ObstacleType.ROCK, ObstacleType.BOX -> {
                    val obstacleTop = obstacle.height
                    if (playerY <= obstacleTop) {
                        return true to false
                    }
                }
            }
        }
        return false to false
    }

private data class CollectResult(val collected: Int, val remaining: List<EggPickup>)

private fun collectEggs(eggs: List<EggPickup>, playerY: Float): CollectResult {
        val playerHeight = 96f
        val playerWidth = 72f
        val playerTop = playerY
        val playerBottom = playerY + playerHeight
        var collected = 0
        val remaining = mutableListOf<EggPickup>()
        eggs.forEach { egg ->
            val overlapX = playerX + playerWidth > egg.x && playerX < egg.x + egg.size
            val overlapY = playerTop < egg.y + egg.size && playerBottom > egg.y
            if (overlapX && overlapY) {
                collected += 1
            } else {
                remaining += egg
            }
        }
        return CollectResult(collected, remaining)
    }

    private fun shouldSpawn(obstacles: List<Obstacle>): Boolean {
        val farthest = obstacles.maxByOrNull { it.x + it.width }
        return farthest == null || farthest.x + farthest.width < viewportWidth * 0.6f
    }

    private fun spawnNext(
        obstacles: List<Obstacle>,
        eggs: List<EggPickup>
    ): Pair<List<Obstacle>, List<EggPickup>> {
        val mutableObstacles = obstacles.toMutableList()
        val mutableEggs = eggs.toMutableList()
        val rng = Random(obstacleSeed++)
        val typeRoll = rng.nextFloat()
        val obstacleType = when {
            typeRoll < 0.4f -> ObstacleType.ROCK
            typeRoll < 0.75f -> ObstacleType.BOX
            else -> ObstacleType.GAP
        }
        val width = when (obstacleType) {
            ObstacleType.ROCK -> 80f
            ObstacleType.BOX -> 120f
            ObstacleType.GAP -> 180f
        }
        val height = when (obstacleType) {
            ObstacleType.ROCK -> 60f
            ObstacleType.BOX -> 120f
            ObstacleType.GAP -> 0f
        }
        val spawnX = viewportWidth + rng.nextFloat() * 220f
        mutableObstacles += Obstacle(
            id = obstacleSeed,
            type = obstacleType,
            x = spawnX,
            width = width,
            height = height
        )
        val placeEgg = rng.nextBoolean()
        if (placeEgg) {
            mutableEggs += EggPickup(
                id = obstacleSeed,
                x = spawnX + width / 2,
                y = height + 40f
            )
        }
        return mutableObstacles to mutableEggs
    }

    private fun jump(strong: Boolean) {
        val current = currentState
        if (current.phase != GamePhase.Playing) return
        val jumpPower = if (strong) 900f else 720f
        this.currentState = current.copy(playerVelocity = jumpPower)
        viewModelScope.launch { if (current.soundEnabled) audioController.playChickenJump() }
    }

    fun onAppBackgrounded() {
        pause()
    }

    private suspend fun applyVolumes() {
        audioController.setMusicVolume(if (settingsRepository.musicEnabled.first()) 100 else 0)
        audioController.setSoundVolume(if (settingsRepository.soundEnabled.first()) 100 else 0)
    }
}
