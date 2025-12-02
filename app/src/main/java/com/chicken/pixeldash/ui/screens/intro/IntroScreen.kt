package com.chicken.pixeldash.ui.screens.intro

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun IntroOverlay(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000))
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.8f)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF7EF7A),
                            Color(0xFFBDB63D)
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
                .border(3.dp, Color.Black, RoundedCornerShape(28.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GradientText(
                text = "Ready?",
                size = 40.sp,
                stroke = 10f,
                expand = false
            )

            Spacer(Modifier.height(28.dp))

            IntroTip(
                icon = Icons.Default.TouchApp,
                text = "Tap to jump"
            )

            Spacer(Modifier.height(24.dp))

            IntroTip(
                icon = Icons.Default.KeyboardArrowUp,
                text = "Swipe up = high jump"
            )

            Spacer(Modifier.height(24.dp))

            IntroTip(
                icon = Icons.Default.Egg,
                text = "Collect eggs"
            )

            Spacer(Modifier.height(26.dp))

            Image(
                painter = painterResource(id = R.drawable.chicken_2),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(26.dp))

            PixelButton(
                text = "Start",
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(58.dp)
            )
        }
    }
}
@Composable
private fun IntroTip(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        IconOvalButton(
            modifier = Modifier.size(50.dp),
            cornerRadius = 18.dp,
            borderWidth = 3.dp,
            onClick = { },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.fillMaxSize(0.55f)
                )
            }
        )

        GradientText(
            text = text,
            size = 16.sp,
            stroke = 6f,
            expand = true,
            alignment = TextAlign.Start,
        )
    }
}
