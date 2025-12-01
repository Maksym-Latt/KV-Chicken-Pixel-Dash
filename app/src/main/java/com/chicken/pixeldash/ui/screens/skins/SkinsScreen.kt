package com.chicken.pixeldash.ui.screens.skins

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.EggCounter
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.theme.retroFont

@Composable
fun SkinsScreen(
    viewModel: SkinsViewModel,
    onBack: () -> Unit
) {
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
            alpha = 0.6f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixelButton(
                    text = "Back",
                    modifier = Modifier.weight(1f),
                    onClick = onBack
                )
                Spacer(modifier = Modifier.weight(0.1f))
                EggCounter(value = state.coins)
            }
            Spacer(modifier = Modifier.height(12.dp))

            state.skins.forEach { skinUi ->
                SkinCard(
                    skinUiModel = skinUi,
                    canAfford = state.coins >= skinUi.skin.price,
                    onChoose = { viewModel.onChooseSkin(skinUi) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SkinCard(
    skinUiModel: SkinUiModel,
    canAfford: Boolean,
    onChoose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xDDF5E3A1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = skinUiModel.skin.name.uppercase(),
                fontFamily = retroFont,
                fontSize = 18.sp,
                color = Color(0xFF1F1200),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = skinUiModel.skin.drawable),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when {
                skinUiModel.selected -> {
                    Surface(color = Color(0xFF90EE90), shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = "Chosen",
                            fontFamily = retroFont,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D4D00),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                skinUiModel.owned -> {
                    PixelButton(text = "Choose", onClick = onChoose)
                }

                else -> {
                    PixelButton(
                        text = "${skinUiModel.skin.price} eggs",
                        enabled = canAfford,
                        onClick = onChoose,
                        background = if (canAfford) Color(0xFFFFB347) else Color(0x88AAAAAA)
                    )
                }
            }
        }
    }
}
