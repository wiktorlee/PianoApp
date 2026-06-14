package com.example.instrumenttrainer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppExtraColors(
    val backgroundBase: Color,
    val radialTop: List<Color>,
    val radialSide: List<Color>,
    val radialBottom: List<Color>,
    val dotColor: Color,
    val grainColor: Color,
    val vignetteColor: Color,
    val ringTrack: Color,
    val ringGlow: Color,
    val navBarBackground: Color,
    val navBarSelectedPill: Color,
    val cardStrong: Color,
    val cardSubtle: Color,
    val cardBorder: Color,
)

val LocalAppExtraColors = staticCompositionLocalOf {
    AppExtraColors(
        backgroundBase = BrandBgLight,
        radialTop = listOf(BrandCoral.withAlpha(0.16f), Color.Transparent),
        radialSide = listOf(BrandGraphiteSurface, Color.Transparent),
        radialBottom = listOf(BrandMint.withAlpha(0.28f), Color.Transparent),
        dotColor = BrandGraphite.withAlpha(0.18f),
        grainColor = BrandGraphite.withAlpha(0.1f),
        vignetteColor = BrandMistBg.withAlpha(0.18f),
        ringTrack = BrandGraphite.withAlpha(0.16f),
        ringGlow = BrandCoral.withAlpha(0.28f),
        navBarBackground = BrandMistBg,
        navBarSelectedPill = BrandGraphiteSoft,
        cardStrong = BrandSurfaceCard,
        cardSubtle = BrandSurfacePanel,
        cardBorder = BrandBorder,
    )
}

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

private val DarkColorScheme = darkColorScheme(
    primary = BrandCoral,
    onPrimary = Color.White,
    primaryContainer = BrandCoral.copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFFF5D6D4),
    secondary = BrandMint,
    onSecondary = BrandGraphite,
    background = BrandBgDark,
    onBackground = BrandBgLight,
    surface = Color(0xFF3E4246),
    onSurface = BrandBgLight,
    surfaceVariant = Color(0xFF454A4E),
    onSurfaceVariant = BrandMistBg.copy(alpha = 0.92f),
    outline = Color.White.copy(alpha = 0.16f),
)

private val LightExtraColors = AppExtraColors(
    backgroundBase = BrandBgLight,
    radialTop = listOf(BrandCoral.withAlpha(0.16f), Color.Transparent),
    radialSide = listOf(BrandGraphite.withAlpha(0.08f), Color.Transparent),
    radialBottom = listOf(BrandMint.withAlpha(0.28f), Color.Transparent),
    dotColor = BrandGraphite.withAlpha(0.18f),
    grainColor = BrandGraphite.withAlpha(0.1f),
    vignetteColor = BrandMistBg.withAlpha(0.18f),
    ringTrack = BrandGraphite.withAlpha(0.16f),
    ringGlow = BrandCoral.withAlpha(0.28f),
    navBarBackground = BrandMistBg,
    navBarSelectedPill = BrandGraphiteSoft,
    cardStrong = BrandSurfaceCard,
    cardSubtle = BrandSurfacePanel,
    cardBorder = BrandBorder,
)

private val DarkExtraColors = AppExtraColors(
    backgroundBase = BrandBgDark,
    radialTop = listOf(BrandCoral.withAlpha(0.14f), Color.Transparent),
    radialSide = listOf(BrandCoral.withAlpha(0.1f), Color.Transparent),
    radialBottom = listOf(BrandMint.withAlpha(0.12f), Color.Transparent),
    dotColor = Color.White.copy(alpha = 0.038f),
    grainColor = Color.White.copy(alpha = 0.014f),
    vignetteColor = Color(0xFF141618).copy(alpha = 0.34f),
    ringTrack = Color.White.copy(alpha = 0.12f),
    ringGlow = BrandCoral.withAlpha(0.2f),
    navBarBackground = BrandNavDark,
    navBarSelectedPill = Color(0xFF565B60),
    cardStrong = Color(0xFF454A4E),
    cardSubtle = Color(0xFF3A3E42),
    cardBorder = Color.White.copy(alpha = 0.14f),
)

@Composable
fun InstrumentTrainerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(LocalAppExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

object AppTheme {
    val extra: AppExtraColors
        @Composable
        get() = LocalAppExtraColors.current
}
