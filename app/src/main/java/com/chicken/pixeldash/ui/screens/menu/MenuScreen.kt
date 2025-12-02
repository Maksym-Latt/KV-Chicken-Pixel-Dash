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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.chicken.pixeldash.ui.components.PlayButton
import com.chicken.pixeldash.ui.theme.retroFont
@Composable
fun MenuScreen(
    viewModel: MenuViewModel,
    onPlay: () -> Unit,
    onSkins: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            tween(950, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = ""
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
            alpha = 0.7f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(2f))

            GradientText(
                text = "CHICKEN",
                size = 46.sp,
                stroke = 15f,
                strokeColor = Color(0xFF1C1C1C)
            )

            GradientText(
                text = "PIXEL",
                size = 32.sp,
                stroke = 15f,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFEBE76E), Color(0xFFAEAB26))
                ),
                strokeColor = Color(0xFF1C1C1C)
            )

            GradientText(
                text = "DASH",
                size = 32.sp,
                stroke = 15f,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF8B8942), Color(0xFF727019))
                ),
                strokeColor = Color(0xFF1C1C1C)
            )

            Spacer(modifier = Modifier.weight(3f))

            PlayButton(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(1.2f),
                onClick = onPlay
            )

            Spacer(modifier = Modifier.weight(3f))

            Box(
                modifier = Modifier
                    .size(110.dp)
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

            Spacer(modifier = Modifier.weight(1.2f))

            PixelButton(
                text = "Shop",
                onClick = onSkins,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(54.dp)
            )

            Spacer(modifier = Modifier.weight(0.7f))
        }
    }
}
