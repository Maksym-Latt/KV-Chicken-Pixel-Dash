package com.chicken.pixeldash.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelSwitch


@Composable
internal fun MenuSettingsOverlay(
    musicEnabled: Boolean,
    soundsEnabled: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSounds: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(0.8f),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFF7EF7A),
                                Color(0xFFBDB63D)
                            )
                        ),
                        RoundedCornerShape(28.dp)
                    )
                    .border(4.dp, Color.Black, RoundedCornerShape(28.dp))
                    .padding(26.dp),
                horizontalAlignment = Alignment.Start
            ) {

                GradientText(
                    text = "Settings",
                    size = 30.sp,
                    stroke = 10f,
                )

                Spacer(Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GradientText(
                        text = "Music",
                        size = 22.sp,
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
                        size = 22.sp,
                        stroke = 8f,
                        expand = false
                    )

                    PixelSwitch(
                        enabled = soundsEnabled,
                        onToggle = onToggleSounds
                    )
                }
            }

            IconOvalButton(
                modifier = Modifier
                    .offset(x = 24.dp, y = (-24).dp)
                    .size(58.dp),
                cornerRadius = 36.dp,
                borderWidth = 4.dp,
                onClick = onClose,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize(0.55f)
                    )
                }
            )
        }
    }
}