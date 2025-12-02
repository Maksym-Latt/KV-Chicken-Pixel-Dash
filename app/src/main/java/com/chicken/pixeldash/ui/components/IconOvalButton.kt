package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconOvalButton(
    modifier: Modifier = Modifier,
    icon: @Composable BoxScope.() -> Unit,
    onClick: () -> Unit,
    cornerRadius: Dp = 26.dp,
    borderWidth: Dp = 1.dp,
    gradient: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFE86A),
            Color(0xFFC9B637)
        )
    )
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(gradient)
            .border(borderWidth, Color.Black, RoundedCornerShape(cornerRadius))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}