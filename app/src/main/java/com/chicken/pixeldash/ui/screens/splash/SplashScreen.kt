package com.chicken.pixeldash.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.GradientText
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(onFinished: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

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
            alpha = 0.65f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.weight(1f))

            GradientText(
                text = "CHICKEN",
                size = 42.sp,
                stroke = 14f,
                strokeColor = Color(0xFF1C1C1C),
            )

            GradientText(
                text = "PIXEL",
                size = 30.sp,
                stroke = 14f,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFFEBE76E), Color(0xFFAEAB26))
                ),
                strokeColor = Color(0xFF1C1C1C)
            )

            GradientText(
                text = "DASH",
                size = 30.sp,
                stroke = 14f,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF8B8942), Color(0xFF727019))
                ),
                strokeColor = Color(0xFF1C1C1C)
            )

            Spacer(Modifier.weight(2.5f))

            Image(
                painter = painterResource(id = R.drawable.chicken_1),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.weight(2.5f))

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFFE6DB6F),
                trackColor = Color(0xFF2F6A8A)
            )

            Spacer(Modifier.weight(1f))
        }
    }
}
