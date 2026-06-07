package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.instrumenttrainer.ui.theme.BrandBgLight
import com.example.instrumenttrainer.ui.theme.BrandMint
import com.example.instrumenttrainer.ui.theme.BrandMistBg

@Composable
fun AppScreenBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandBgLight),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandMint.copy(alpha = 0.35f),
                            Color.Transparent,
                        ),
                        center = Offset(900f, -120f),
                        radius = 700f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandMistBg.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                        center = Offset(-200f, 400f),
                        radius = 650f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            BrandGraphiteVignette,
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                ),
        )
    }
}

private val BrandGraphiteVignette = Color(0x083C3F41)
