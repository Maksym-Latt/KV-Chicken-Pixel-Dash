package com.chicken.pixeldash.ui.screens.skins

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chicken.pixeldash.R
import com.chicken.pixeldash.ui.components.ArrowLeft
import com.chicken.pixeldash.ui.components.ArrowRight
import com.chicken.pixeldash.ui.components.EggCounter
import com.chicken.pixeldash.ui.components.GradientText
import com.chicken.pixeldash.ui.components.IconOvalButton
import com.chicken.pixeldash.ui.components.PixelButton
import com.chicken.pixeldash.ui.components.PlayButton
import com.chicken.pixeldash.ui.components.PriceButton
import com.chicken.pixeldash.ui.theme.retroFont


@Composable
fun SkinsScreen(
    viewModel: SkinsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val current = state.currentSkin ?: return

    var showNotEnoughDialog by remember { mutableStateOf(false) }

    if (showNotEnoughDialog) {
        AlertDialog(
            onDismissRequest = { showNotEnoughDialog = false },
            confirmButton = {
                PixelButton(
                    text = "OK",
                    modifier = Modifier.width(120.dp),
                    onClick = { showNotEnoughDialog = false }
                )
            },
            title = {
                Text(
                    text = "Not enough eggs!",
                    fontFamily = retroFont,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2F1A00),
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    text = "You don't have enough eggs to buy this skin.",
                    fontFamily = retroFont,
                    color = Color(0xFF3C2A00),
                    fontSize = 14.sp
                )
            },
            containerColor = Color(0xFFF8E7C5)
        )
    }

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.15f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconOvalButton(
                    modifier = Modifier.size(60.dp),
                    onClick = onBack,
                    cornerRadius = 22.dp,
                    borderWidth = 3.dp,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize(0.55f)
                        )
                    }
                )

                EggCounter(value = state.coins)
            }

            // ───────────────────────────────
            Column(
                modifier = Modifier.weight(0.20f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val parts = current.skin.name.uppercase().split(" ")

                if (parts.size >= 2) {
                    GradientText(
                        text = parts[0], size = 46.sp, stroke = 12f,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFF4F177), Color(0xFFD0CC4D)
                            )
                        ),
                    )
                    GradientText(
                        text = parts.drop(1).joinToString(" "), size = 46.sp, stroke = 12f,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFC8C543), Color(0xFFA29F17)
                            )
                        ),
                    )
                } else {
                    GradientText(
                        text = current.skin.name.uppercase(), size = 26.sp, stroke = 12f,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFF4F177), Color(0xFFD0CC4D)
                            )
                        ),
                    )
                }
            }

            // ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f),
                contentAlignment = Alignment.Center
            ) {
                ArrowLeft(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(48.dp)
                        .clickable { viewModel.prevSkin() }
                )

                Image(
                    painter = painterResource(id = current.skin.drawable),
                    contentDescription = null,
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Fit
                )

                ArrowRight(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp)
                        .clickable { viewModel.nextSkin() }
                )
            }

            // ───────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.25f),
                contentAlignment = Alignment.TopCenter
            ) {

                when {
                    current.selected -> {
                        PixelButton(
                            text = "Chosen",
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(56.dp),
                            background = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF66A84E), Color(
                                        0xFF5CA835
                                    )
                                )
                            ),
                            onClick = {}
                        )
                    }

                    current.owned -> {
                        PixelButton(
                            text = "Choose",
                            modifier = Modifier
                                .fillMaxWidth(0.45f)
                                .height(56.dp),
                            background = Brush.verticalGradient(
                                listOf(
                                    Color(0xFF91F06E), Color(
                                        0xFF4BA41E
                                    )
                                )
                            ),
                            onClick = { viewModel.onChooseSkin(current) }
                        )
                    }

                    else -> {
                        val canBuy = state.coins >= current.skin.price

                        PriceButton(
                            price = current.skin.price,
                            modifier = Modifier.fillMaxWidth(0.45f),
                            brush =
                                if (canBuy)
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFFFC551),
                                            Color(0xFFF39A23)
                                        )
                                    )
                                else
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFFBFBFBF),
                                            Color(0xFF8C8C8C)
                                        )
                                    ),
                            onClick = {
                                if (canBuy) viewModel.onChooseSkin(current)
                                else showNotEnoughDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}