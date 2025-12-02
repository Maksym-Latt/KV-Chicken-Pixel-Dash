package com.chicken.pixeldash.ui.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.EggCounter
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.ScoreCounter
import com.chicken.pixeldash.ui.screens.intro.IntroOverlay
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExit: () -> Unit,
    showIntroOnLaunch: Boolean = true
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.handleResumeLifecycle()
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseFromLifecycle()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BackHandler {
        if (state.status == GameStatus.Running) {
            viewModel.pauseGame()
        } else {
            viewModel.onExit()
            onExit()
        }
    }

    LaunchedEffect(showIntroOnLaunch) { viewModel.startNewGame(showIntroOnLaunch) }

    var startY by remember { mutableStateOf<Float?>(null) }
    var hasJumped by remember { mutableStateOf(false) }

    Surface(color = Color(0xFF74C2E4)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { startY = it.y; hasJumped = false },
                        onDragCancel = { startY = null },
                        onDragEnd = { startY = null }
                    ) { change, _ ->
                        if (startY != null && !hasJumped) {
                            val dy = startY!! - change.position.y
                            if (dy > 70f) {
                                viewModel.jump(true)
                                hasJumped = true
                                startY = null
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { viewModel.jump(false) }
                }
        ) {
            val groundHeight = state.groundHeight.dp
            val groundTop = maxHeight - groundHeight

            LaunchedEffect(maxWidth, maxHeight) {
                viewModel.onViewportChanged(maxWidth.value, maxHeight.value)
            }

            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconOvalButton(
                    modifier = Modifier.size(60.dp),
                    onClick = viewModel::pauseGame,
                    cornerRadius = 22.dp,
                    borderWidth = 3.dp,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize(0.55f)
                        )
                    }
                )

                Column(horizontalAlignment = Alignment.End) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ScoreCounter(value = state.score)
                    Spacer(modifier = Modifier.height(4.dp))
                    EggCounter(value = state.eggsCollected)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                state.obstacles.forEach { o ->
                    Sprite(
                        painter = painterResource(
                            if (o.type == EntityType.Box) R.drawable.item_box
                            else R.drawable.item_rock
                        ),
                        spriteSize = o.spriteSize(),
                        x = o.x,
                        groundTop = groundTop,
                        spriteY = o.y
                    )
                }

                state.eggs.forEach { egg ->
                    Sprite(
                        painter = painterResource(R.drawable.item_egg),
                        spriteSize = egg.spriteSize(),
                        x = egg.x,
                        groundTop = groundTop,
                        spriteY = egg.y
                    )
                }

                Sprite(
                    painter = painterResource(id = state.skin.drawable),
                    spriteSize = playerSpriteSize(),
                    x = PLAYER_X,
                    groundTop = groundTop,
                    spriteY = state.playerY,
                    bouncing = state.status == GameStatus.Running
                )
            }

            if (state.status == GameStatus.Ready) {
                IntroOverlay(onStart = viewModel::startRun)
            }

            if (state.status == GameStatus.Paused) {
                PauseMenu(
                    onExit = {
                        viewModel.onExit()
                        onExit()
                    },
                    musicEnabled = state.musicEnabled,
                    soundsEnabled = state.soundEnabled,
                    onToggleMusic = viewModel::toggleMusic,
                    onToggleSounds = viewModel::toggleSound,
                    onRestart = viewModel::restart,
                    onResume = viewModel::resumeGame
                )
            }


            if (state.status == GameStatus.Over) {
                GameOverScreen(
                    score = state.score,
                    eggs = state.eggsCollected,
                    skinDrawable = state.skin.drawable,
                    onRestart = viewModel::restart,
                    onMenu = {
                        viewModel.onExit()
                        onExit()
                    }
                )
            }
        }
    }
}


@Composable
private fun BoxScope.Sprite(
    painter: Painter,
    spriteSize: Pair<Float, Float>,
    x: Float,
    groundTop: Dp,
    spriteY: Float,
    bouncing: Boolean = false,
    showHitbox: Boolean = false,
    hitbox: Rect? = null
) {
    val width = spriteSize.first.dp
    val height = spriteSize.second.dp
    val yOffset = groundTop - height - spriteY.dp
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(width = width, height = height)
            .offset(x = x.dp, y = yOffset)
            .graphicsLayer {
                if (bouncing) {
                    translationY = (-spriteY * 0.04f)
                }
            },
        contentScale = ContentScale.Crop
    )

    if (showHitbox && hitbox != null) {
        val hitboxWidth = (hitbox.right - hitbox.left).dp
        val hitboxHeight = (hitbox.bottom - hitbox.top).dp
        val hitboxYOffset = groundTop - hitboxHeight - hitbox.top.dp
        Box(
            modifier = Modifier
                .offset(x = hitbox.left.dp, y = hitboxYOffset)
                .size(hitboxWidth, hitboxHeight)
                .border(1.dp, Color.Red, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
private fun OverlayCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFBF5E9)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontFamily = retroFont,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = Color(0xFF1C0F00),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    fontFamily = retroFont,
                    fontSize = 12.sp,
                    color = Color(0xFF2B1A00),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }
}
