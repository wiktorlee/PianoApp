package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.ui.theme.AppTheme

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    radius: Dp = 28.dp,
    contentPadding: Dp = 16.dp,
    verticalSpacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    val extra = AppTheme.extra
    val fill = if (strong) extra.cardStrong else extra.cardSubtle

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radius),
        color = fill,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = if (strong) 3.dp else 2.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, extra.cardBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        ) {
            content()
        }
    }
}
