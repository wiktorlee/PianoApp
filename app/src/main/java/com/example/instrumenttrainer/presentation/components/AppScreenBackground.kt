package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.ui.theme.AppTheme

@Composable
fun AppScreenBackground(
    modifier: Modifier = Modifier,
) {
    val extra = AppTheme.extra
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(extra.backgroundBase),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = extra.radialTop,
                        center = Offset(with(density) { 320.dp.toPx() }, with(density) { (-80).dp.toPx() }),
                        radius = with(density) { 380.dp.toPx() },
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = extra.radialSide,
                        center = Offset(with(density) { (-60).dp.toPx() }, with(density) { 220.dp.toPx() }),
                        radius = with(density) { 340.dp.toPx() },
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = extra.radialBottom,
                        center = Offset(with(density) { 300.dp.toPx() }, with(density) { 680.dp.toPx() }),
                        radius = with(density) { 360.dp.toPx() },
                    ),
                ),
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 14.dp.toPx()
            val dotRadiusLarge = 1.2.dp.toPx()
            val dotRadiusSmall = 0.9.dp.toPx()
            var y = 0f
            var row = 0
            while (y < size.height) {
                var x = 0f
                var col = 0
                while (x < size.width) {
                    val offsetX = if (col % 2 == 0) 2.dp.toPx() else 9.dp.toPx()
                    val offsetY = if (row % 2 == 0) 2.dp.toPx() else 8.dp.toPx()
                    drawCircle(
                        color = extra.dotColor,
                        radius = if (col % 2 == 0) dotRadiusLarge else dotRadiusSmall,
                        center = Offset(x + offsetX, y + offsetY),
                    )
                    x += spacing
                    col++
                }
                y += spacing
                row++
            }

            val grainSpacing = 8.dp.toPx()
            var gy = 0f
            while (gy < size.height) {
                var gx = 0f
                while (gx < size.width) {
                    drawRect(
                        color = extra.grainColor,
                        topLeft = Offset(gx, gy),
                        size = androidx.compose.ui.geometry.Size(1.dp.toPx(), 1.dp.toPx()),
                    )
                    drawRect(
                        color = extra.grainColor,
                        topLeft = Offset(gx + 4.dp.toPx(), gy + 1.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(1.dp.toPx(), 1.dp.toPx()),
                    )
                    gx += grainSpacing
                }
                gy += grainSpacing
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, extra.vignetteColor),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                ),
        )
    }
}
