package com.chicken.pixeldash.ui.components

import android.R.attr.strokeColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.theme.retroFont
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

@Composable
fun EggCounter(
    modifier: Modifier = Modifier,
    value: Int
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GradientText(
            text = value.toString(),
            size = 26.sp,
            stroke = 1f,
            expand = false,
            strokeColor = Color(0xffffffff)
        )
        Image(
            painter = painterResource(id = R.drawable.item_egg),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
    }
}
