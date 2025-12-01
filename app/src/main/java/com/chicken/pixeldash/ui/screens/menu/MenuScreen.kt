package com.chicken.pixeldash.ui.screens.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.EggCounter
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.components.SoundToggle
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onPlay: () -> Unit,
    onSkins: () -> Unit,
    onScores: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF74C2E4))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            GradientText(text = "CHICKEN", size = 38.sp)
            GradientText(text = "PIXEL DASH", size = 36.sp, stroke = 6f)

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .offset(y = bounce.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chicken_1),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Surface(color = Color(0xAAFFFFFF)) {
                Text(
                    text = "Skin: ${state.selectedSkinName}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    fontFamily = retroFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = Color(0xFF1A1200)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            EggCounter(value = state.coins)

            Spacer(modifier = Modifier.height(16.dp))
            PixelButton(text = "Play", onClick = onPlay)
            Spacer(modifier = Modifier.height(8.dp))
            PixelButton(text = "Skins", onClick = onSkins)
            Spacer(modifier = Modifier.height(8.dp))
            PixelButton(text = "Scores", onClick = onScores)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Best: ${state.bestScore}",
                fontFamily = retroFont,
                fontSize = 12.sp,
                color = Color(0xFF1C1200)
            )
            Spacer(modifier = Modifier.height(12.dp))
            SoundToggle(
                musicEnabled = state.musicEnabled,
                soundEnabled = state.soundEnabled,
                onToggleMusic = viewModel::toggleMusic,
                onToggleSound = viewModel::toggleSound
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
