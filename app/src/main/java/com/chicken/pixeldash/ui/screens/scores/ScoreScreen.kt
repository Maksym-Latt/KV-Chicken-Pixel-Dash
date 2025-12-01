package com.chicken.pixeldash.ui.screens.scores

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.screens.menu.MenuViewModel
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun ScoreScreen(viewModel: MenuViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF74C2E4))
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Surface(color = Color(0xAAFFFFFF)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Best Score", fontFamily = retroFont, fontSize = 18.sp, color = Color(0xFF1C1200))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.bestScore.toString(), fontFamily = retroFont, fontSize = 32.sp, color = Color(0xFF1C1200))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Coins: ${state.coins}", fontFamily = retroFont, fontSize = 14.sp, color = Color(0xFF1C1200))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            PixelButton(text = "Back", onClick = onBack)
        }
    }
}
