package com.chicken.pixeldash.ui.components

import android.R.attr.text
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.theme.retroFont
import kotlin.text.uppercase

@Composable
fun PriceButton(
    price: Int,
    modifier: Modifier = Modifier,
    brush: Brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFC551),
            Color(0xFFF39A23)
        )
    ),
    border: Color = Color.Black,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(40.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
            .border(3.dp, border, shape)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GradientText(
                text = price.toString(),
                size = 18.sp,
                stroke = 6f,
                expand = false,
                strokeColor = Color(0xFF1C1C1C),
                alignment = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(6.dp))

            Image(
                painter = painterResource(id = R.drawable.item_egg),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
