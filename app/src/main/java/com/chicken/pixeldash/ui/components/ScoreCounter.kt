package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreCounter(
    modifier: Modifier = Modifier,
    value: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GradientText(
            text = "Score:",
            size = 20.sp,
            stroke = 1f,
            expand = false,
            strokeColor = Color(0xffffffff)
        )

        GradientText(
            text = formatScore(value),
            size = 26.sp,
            stroke = 1f,
            expand = false,
            strokeColor = Color(0xffffffff)
        )
    }
}


fun formatScore(value: Int): String {
    return when {
        value < 1000 -> value.toString()
        value < 1_000_000 -> String.format("%.1fk", value / 1000f).removeSuffix(".0k") + "k"
        value < 1_000_000_000 -> String.format("%.1fM", value / 1_000_000f).removeSuffix(".0M") + "M"
        else -> String.format("%.1fB", value / 1_000_000_000f).removeSuffix(".0B") + "B"
    }
}
