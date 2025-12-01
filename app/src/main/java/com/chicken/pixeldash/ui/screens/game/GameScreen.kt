package com.chicken.pixeldash.ui.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun GameScreen(viewModel: GameViewModel, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.onAppBackgrounded()
            if (event == Lifecycle.Event.ON_RESUME) viewModel.resume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF74C2E4))
    ) {
        LaunchedEffect(maxWidth, maxHeight) {
            viewModel.updateViewport(maxWidth.value, maxHeight.value)
        }

        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { viewModel.onLongTap() },
                        onTap = { viewModel.onTap() }
                    )
                }
        ) {
            Ground()
            ObstaclesLayer(state)
            EggLayer(state)
            val playerOffset = maxWidth * 0.2f
            PlayerLayer(state, playerOffset.value)

            HUD(state = state, onPause = viewModel::pause)

            when (state.phase) {
                GamePhase.Paused -> PauseOverlay(onResume = viewModel::resume, onRestart = viewModel::restart, onExit = onExit)
                GamePhase.GameOver -> GameOverOverlay(
                    score = state.score,
                    best = state.bestScore,
                    onTryAgain = viewModel::restart,
                    onBack = onExit
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun BoxScope.Ground() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .height(120.dp)
            .background(Color(0xFF3E2C13))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFF70C94C))
        )
    }
}

@Composable
private fun BoxScope.HUD(state: GameUiState, onPause: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ScorePill(label = "Score", value = state.score)
            ScorePill(label = "Best", value = state.bestScore)
        }
        Column(horizontalAlignment = Alignment.End) {
            EggCounter(value = state.runEggs)
            Spacer(modifier = Modifier.height(8.dp))
            PixelButton(text = "Pause", modifier = Modifier.width(120.dp), onClick = onPause)
        }
    }
}

@Composable
private fun BoxScope.ObstaclesLayer(state: GameUiState) {
    state.obstacles.forEach { obstacle ->
        val res = when (obstacle.type) {
            ObstacleType.ROCK -> R.drawable.item_rock
            ObstacleType.BOX -> R.drawable.item_box
            ObstacleType.GAP -> null
        }
        if (res != null) {
            Image(
                painter = painterResource(id = res),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = obstacle.x.dp, y = (-obstacle.height).dp)
                    .size(width = obstacle.width.dp, height = obstacle.height.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
private fun BoxScope.EggLayer(state: GameUiState) {
    state.eggs.forEach { egg ->
        Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.BottomStart)
                .offset(x = egg.x.dp, y = (-egg.y).dp)
                .size(28.dp)
        )
    }
}

@Composable
private fun BoxScope.PlayerLayer(state: GameUiState, playerX: Float) {
    Image(
        painter = painterResource(id = state.skin.drawable),
        contentDescription = null,
        modifier = Modifier
            .padding(bottom = 52.dp)
            .align(Alignment.BottomStart)
            .offset(x = playerX.dp, y = (-state.playerY).dp)
            .size(96.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun PauseOverlay(onResume: () -> Unit, onRestart: () -> Unit, onExit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFECD48A)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Paused", fontFamily = retroFont, fontSize = 18.sp, color = Color(0xFF1C1200))
                PixelButton(text = "Continue", onClick = onResume)
                PixelButton(text = "Restart", onClick = onRestart)
                PixelButton(text = "Menu", onClick = onExit)
            }
        }
    }
}

@Composable
private fun GameOverOverlay(score: Int, best: Int, onTryAgain: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 6.dp,
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFECD48A)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "GAME OVER", fontFamily = retroFont, fontSize = 22.sp, color = Color(0xFF1C1200))
                Text(text = "Score: $score", fontFamily = retroFont, fontSize = 14.sp, color = Color(0xFF1C1200))
                Text(text = "Best: $best", fontFamily = retroFont, fontSize = 14.sp, color = Color(0xFF1C1200))
                PixelButton(text = "Try Again", onClick = onTryAgain)
                PixelButton(text = "Back", onClick = onBack)
            }
        }
    }
}
