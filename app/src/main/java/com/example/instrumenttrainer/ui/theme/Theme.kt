package com.example.instrumenttrainer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandCoral,
    onPrimary = Color.White,
    primaryContainer = BrandCoral.copy(alpha = 0.12f),
    onPrimaryContainer = BrandCoralHover,
    secondary = BrandMint,
    onSecondary = BrandGraphite,
    background = BrandBgLight,
    onBackground = BrandGraphite,
    surface = BrandSurfaceCard,
    onSurface = BrandGraphite,
    surfaceVariant = BrandSurfacePanel,
    onSurfaceVariant = BrandTextSecondary,
    outline = BrandBorderStrong,
)

@Composable
fun InstrumentTrainerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
