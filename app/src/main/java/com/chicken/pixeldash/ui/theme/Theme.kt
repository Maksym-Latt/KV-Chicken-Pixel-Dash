package com.chicken.pixeldash.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RetroYellow,
    secondary = RetroPanel,
    tertiary = RetroGrass,
    background = RetroSky,
    surface = RetroPanel,
    onPrimary = RetroDark,
    onSecondary = RetroDark,
    onTertiary = RetroDark,
    onBackground = RetroDark,
    onSurface = RetroDark
)

@Composable
fun ChickenPixelDashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
