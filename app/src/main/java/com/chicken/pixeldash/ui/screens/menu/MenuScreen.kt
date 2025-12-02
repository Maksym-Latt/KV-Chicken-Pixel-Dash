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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.components.PixelSwitch
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
    var showSettings by remember { mutableStateOf(false) }

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

            Spacer(modifier = Modifier.height(14.dp))

            PixelButton(
                text = "Settings",
                onClick = { showSettings = true },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(54.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color(0xFF1C1C1C),
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.weight(0.4f))
        }

        if (showSettings) {
            MenuSettingsOverlay(
                musicEnabled = state.musicEnabled,
                soundsEnabled = state.soundEnabled,
                onToggleMusic = viewModel::toggleMusic,
                onToggleSounds = viewModel::toggleSound,
                onClose = { showSettings = false }
            )
        }
    }
}

@Composable
private fun MenuSettingsOverlay(
    musicEnabled: Boolean,
    soundsEnabled: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSounds: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
    ) {
        IconOvalButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 40.dp)
                .size(64.dp),
            cornerRadius = 40.dp,
            borderWidth = 4.dp,
            onClick = onClose,
            icon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(34.dp)
                )
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(280.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF7EF7A),
                            Color(0xFFBDB63D)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
                .border(6.dp, Color.Black, RoundedCornerShape(28.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.Start
        ) {

            GradientText(
                text = "Settings",
                size = 36.sp,
                stroke = 10f,
                expand = false
            )

            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientText(
                    text = "Music",
                    size = 26.sp,
                    stroke = 8f,
                    expand = false
                )

                PixelSwitch(
                    enabled = musicEnabled,
                    onToggle = onToggleMusic
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientText(
                    text = "Sounds",
                    size = 26.sp,
                    stroke = 8f,
                    expand = false
                )

                PixelSwitch(
                    enabled = soundsEnabled,
                    onToggle = onToggleSounds
                )
            }
        }
    }
}
