package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun PixelButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: Color = Color(0xFFFFE66D),
    border: Color = Color(0xFF5A3C00),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(3.dp, border),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = Color(0xFF1F1200),
            disabledContainerColor = background.copy(alpha = 0.5f),
            disabledContentColor = Color(0x991F1200)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = retroFont,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}
