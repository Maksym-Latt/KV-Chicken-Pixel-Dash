package com.chicken.pixeldash.ui.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelButton


@Composable
fun GameOverScreen(
    score: Int,
    eggs: Int,
    skinDrawable: Int,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            GradientText(
                text = "GAME OVER",
                size = 46.sp,
                stroke = 14f,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFF199), Color(0xFFD4C95C))
                )
            )

            Spacer(Modifier.height(12.dp))

            GradientText(
                text = "Score : $score",
                size = 22.sp,
                stroke = 8f
            )

            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(id = skinDrawable),
                contentDescription = null,
                modifier = Modifier.size(180.dp)
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                GradientText(
                    text = "+$eggs",
                    size = 22.sp,
                    expand = false,
                    stroke = 8f
                )
                Spacer(Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.item_egg),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.height(22.dp))

            PixelButton(
                text = "Retry",
                modifier = Modifier.fillMaxWidth(0.6f),
                onClick = onRestart
            )

            Spacer(Modifier.height(12.dp))

            PixelButton(
                text = "Menu",
                modifier = Modifier.fillMaxWidth(0.6f),
                onClick = onMenu
            )
        }
    }
}
