package com.chicken.pixeldash.ui.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.components.ScorePill
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onExit: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showHitboxes by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.resumeAfterPause()
                Lifecycle.Event.ON_PAUSE -> viewModel.pauseFromLifecycle()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.restart()
    }

    Surface(color = Color(0xFF74C2E4)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { viewModel.jump(high = false) },
                        onLongPress = { viewModel.jump(high = true) }
                    )
                }
        ) {
            val groundHeight = state.groundHeight.dp
            val groundTop = maxHeight - groundHeight
            val density = LocalDensity.current

            LaunchedEffect(maxWidth, maxHeight) {
                viewModel.onViewportChanged(maxWidth.value, maxHeight.value)
            }

            val rockPainter = painterResource(id = R.drawable.item_rock)
            val boxPainter = painterResource(id = R.drawable.item_box)
            val eggPainter = painterResource(id = R.drawable.item_egg)
            val chickenPainter = painterResource(id = state.skin.drawable)

            LaunchedEffect(rockPainter, boxPainter, eggPainter, chickenPainter, density) {
                fun register(type: EntityType, painter: androidx.compose.ui.graphics.painter.Painter) {
                    val intrinsic = painter.intrinsicSize
                    if (intrinsic == Size.Unspecified) return
                    val widthDp = with(density) { intrinsic.width.toDp().value }
                    val heightDp = with(density) { intrinsic.height.toDp().value }
                    viewModel.setSpriteDimensions(type, widthDp, heightDp)
                }

                register(EntityType.Rock, rockPainter)
                register(EntityType.Box, boxPainter)
                register(EntityType.Egg, eggPainter)
                register(EntityType.Player, chickenPainter)
            }

            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.7f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScorePill(label = "Score", value = state.score)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        ScorePill(label = "Best", value = state.bestScore)
                        EggCounter(value = state.eggsCollected)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelButton(
                        text = if (state.status == GameStatus.Paused) "Resume" else "Pause",
                        onClick = viewModel::togglePause,
                        modifier = Modifier.weight(1f)
                    )
                    PixelButton(
                        text = "Menu",
                        onClick = {
                            viewModel.onExit()
                            onExit()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PixelButton(
                        text = if (showHitboxes) "Hide Hitboxes" else "Show Hitboxes",
                        onClick = { showHitboxes = !showHitboxes },
                        modifier = Modifier.weight(1f)
                    )
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                Ground(groundHeight = groundHeight)

                state.obstacles.forEach { obstacle ->
                    val painter = when (obstacle.type) {
                        EntityType.Box -> painterResource(id = R.drawable.item_box)
                        else -> painterResource(id = R.drawable.item_rock)
                    }
                    Sprite(
                        painter = painter,
                        x = obstacle.x.dp,
                        groundTop = groundTop,
                        spriteY = obstacle.y.dp,
                        sizeScale = obstacle.sizeScale,
                        colliderScale = obstacle.colliderScale,
                        showHitbox = showHitboxes
                    )
                }

                state.eggs.forEach { egg ->
                    Sprite(
                        painter = painterResource(id = R.drawable.item_egg),
                        x = egg.x.dp,
                        groundTop = groundTop,
                        spriteY = egg.y.dp,
                        sizeScale = egg.sizeScale,
                        colliderScale = egg.colliderScale,
                        showHitbox = showHitboxes
                    )
                }
                Sprite(
                    painter = chickenPainter,
                    x = PLAYER_X.dp,
                    groundTop = groundTop,
                    spriteY = state.playerY.dp,
                    sizeScale = PLAYER_SIZE_SCALE,
                    colliderScale = PLAYER_COLLIDER_SCALE,
                    showHitbox = showHitboxes,
                    bouncing = state.status == GameStatus.Running
                )

                if (state.status == GameStatus.Paused) {
                    OverlayCard(title = "Paused", subtitle = "Tap resume to keep running") {
                        PixelButton(text = "Resume", onClick = viewModel::togglePause)
                        Spacer(modifier = Modifier.height(6.dp))
                        PixelButton(text = "Restart", onClick = viewModel::restart)
                    }
                }

                if (state.status == GameStatus.Over) {
                    OverlayCard(title = "Game Over", subtitle = "Score ${state.score}") {
                        Text(
                            text = "Eggs: ${state.eggsCollected}",
                            fontFamily = retroFont,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color(0xFF1F1500)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PixelButton(text = "Retry", onClick = viewModel::restart)
                        Spacer(modifier = Modifier.height(6.dp))
                        PixelButton(text = "Menu", onClick = {
                            viewModel.onExit()
                            onExit()
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.Sprite(
    painter: androidx.compose.ui.graphics.painter.Painter,
    x: Dp,
    groundTop: Dp,
    spriteY: Dp,
    sizeScale: Float = 1f,
    colliderScale: Float = 1f,
    showHitbox: Boolean = false,
    bouncing: Boolean = false
) {
    val intrinsicSize = painter.intrinsicSize
    if (intrinsicSize == Size.Unspecified) return

    val baseWidth = with(LocalDensity.current) { intrinsicSize.width.toDp() }
    val baseHeight = with(LocalDensity.current) { intrinsicSize.height.toDp() }
    val scaledWidth = baseWidth * sizeScale
    val scaledHeight = baseHeight * sizeScale
    val yOffset = groundTop - scaledHeight - spriteY
    val colliderWidth = scaledWidth * colliderScale
    val colliderHeight = scaledHeight * colliderScale
    val colliderOffsetX = (scaledWidth - colliderWidth) / 2f
    val colliderOffsetY = (scaledHeight - colliderHeight) / 2f

    Box(
        modifier = Modifier
            .offset(x = x, y = yOffset)
            .size(width = scaledWidth, height = scaledHeight)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (bouncing) {
                        translationY = (-spriteY.value * 0.04f)
                    }
                },
            contentScale = ContentScale.Fit
        )

        if (showHitbox) {
            Box(
                modifier = Modifier
                    .offset(x = colliderOffsetX, y = colliderOffsetY)
                    .size(width = colliderWidth, height = colliderHeight)
                    .border(width = 1.dp, color = Color.Red)
                    .background(Color.Red.copy(alpha = 0.25f))
            )
        }
    }
}

@Composable
private fun BoxScope.Ground(groundHeight: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(groundHeight)
            .align(Alignment.BottomCenter)
            .background(Color(0xFF4F993A))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(18) {
                Box(
                    modifier = Modifier
                        .height(18.dp)
                        .weight(1f)
                        .background(
                            color = if (it % 2 == 0) Color(0xFF66B44A) else Color(0xFF5CA646),
                            shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                        )
                )
            }
        }
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
