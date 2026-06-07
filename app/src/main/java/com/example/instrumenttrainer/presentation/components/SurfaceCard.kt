package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.ui.theme.BrandBorder
import com.example.instrumenttrainer.ui.theme.BrandSurfaceCard
import com.example.instrumenttrainer.ui.theme.BrandSurfacePanel

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    radius: Dp = 28.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val fill = if (strong) BrandSurfaceCard else BrandSurfacePanel
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = fill,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, BrandBorder),
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
