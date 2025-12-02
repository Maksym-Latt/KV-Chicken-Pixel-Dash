package com.chicken.pixeldash.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelSwitch

@Composable
fun PauseMenu(
    musicEnabled: Boolean,
    soundsEnabled: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSounds: () -> Unit,
    onRestart: () -> Unit,
    onResume: () -> Unit,
    onExit: () -> Unit
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
            onClick = onResume,
            icon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize(0.55f)
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
                text = "Paused",
                size = 40.sp,
                stroke = 12f,
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

            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                IconOvalButton(
                    modifier = Modifier.size(90.dp),
                    cornerRadius = 26.dp,
                    borderWidth = 4.dp,
                    onClick = onRestart,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize(0.65f)
                        )
                    }
                )

                IconOvalButton(
                    modifier = Modifier.size(90.dp),
                    cornerRadius = 26.dp,
                    borderWidth = 4.dp,
                    onClick = onExit,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                )
            }
        }
    }
}