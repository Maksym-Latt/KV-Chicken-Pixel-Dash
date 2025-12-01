package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R

@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,

    expand: Boolean = true,
    alignment: TextAlign = TextAlign.Center,

    size: TextUnit = 48.sp,
    stroke: Float = 8f,
    strokeColor: Color = Color(0xff332000),

    brush: Brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xffffffff),
            Color(0xffffffff),
            Color(0xfffffffe)
        )
    )
) {
    val typeFace = remember {
        androidx.compose.ui.text.font.FontFamily(
            Font(
                R.font.press_start_2p_regular,
                weight = FontWeight.ExtraBold
            )
        )
    }

    val baseStyle = MaterialTheme.typography.displayLarge.copy(
        fontSize = size,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = typeFace,
        textAlign = alignment
    )

    val widthModifier = if (expand) Modifier.fillMaxWidth() else Modifier

    Box(modifier = modifier) {

        val gradientText = buildAnnotatedString {
            withStyle(style = SpanStyle(brush = brush)) {
                append(text)
            }
        }

        Text(
            text = text,
            style = baseStyle.copy(
                color = strokeColor,
                drawStyle = Stroke(
                    width = stroke,
                    join = StrokeJoin.Round
                )
            ),
            modifier = widthModifier
        )

        Text(
            text = gradientText,
            style = baseStyle,
            color = Color.White,
            modifier = widthModifier
        )
    }
}
