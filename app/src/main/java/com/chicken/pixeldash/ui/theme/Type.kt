package com.chicken.pixeldash.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R

val retroFont = FontFamily(
    Font(R.font.press_start_2p_regular, weight = FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = retroFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    displayLarge = TextStyle(
        fontFamily = retroFont,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = retroFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    )
)
