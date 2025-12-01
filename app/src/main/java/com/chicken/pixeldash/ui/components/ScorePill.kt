package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun ScorePill(
    modifier: Modifier = Modifier,
    label: String,
    value: Int
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xAAFFFFFF)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$label:",
                fontFamily = retroFont,
                fontSize = 12.sp,
                color = Color(0xFF2A1B00)
            )
            Text(
                text = value.toString(),
                fontFamily = retroFont,
                fontSize = 16.sp,
                color = Color(0xFF2A1B00)
            )
        }
    }
}
