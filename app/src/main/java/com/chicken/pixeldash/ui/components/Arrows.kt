package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun ArrowLeft(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawPath(
            path = Path().apply {
                moveTo(w * 0.70f, h * 0.20f)
                lineTo(w * 0.30f, h * 0.50f)
                lineTo(w * 0.70f, h * 0.80f)
            },
            color = Color.Black,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ArrowRight(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawPath(
            path = Path().apply {
                moveTo(w * 0.30f, h * 0.20f)
                lineTo(w * 0.70f, h * 0.50f)
                lineTo(w * 0.30f, h * 0.80f)
            },
            color = Color.Black,
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
    }
}
