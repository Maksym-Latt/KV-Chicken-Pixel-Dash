package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PixelSwitch(
    enabled: Boolean,
    onToggle: () -> Unit
) {
    val bg = if (enabled)
        Brush.verticalGradient(listOf(Color(0xFFFFE86A), Color(0xFFC9B637)))
    else
        Brush.verticalGradient(listOf(Color(0xFFC9C9C9), Color(0xFF8F8F8F)))

    val knobColor = if (enabled) Color(0xFFFFA800) else Color(0xFF555555)

    val alignment = if (enabled) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .width(64.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(3.dp, Color.Black, RoundedCornerShape(50))
            .clickable { onToggle() },
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(knobColor)
                .border(3.dp, Color.Black, CircleShape)
        )
    }
}
