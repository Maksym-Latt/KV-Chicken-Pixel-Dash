package com.chicken.pixeldash.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SoundToggle(
    modifier: Modifier = Modifier,
    musicEnabled: Boolean,
    soundEnabled: Boolean,
    onToggleMusic: () -> Unit,
    onToggleSound: () -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color(0xAAFFFFFF)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleMusic) {
                Icon(
                    imageVector = if (musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = null,
                    tint = Color(0xFF1C1200),
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onToggleSound) {
                Icon(
                    imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = null,
                    tint = Color(0xFF1C1200),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
