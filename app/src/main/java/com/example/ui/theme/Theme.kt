package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GermanGold,
    onPrimary = GermanBlack,
    primaryContainer = Slate800,
    onPrimaryContainer = GermanGoldLight,
    secondary = AIElectricCyan,
    onSecondary = GermanBlack,
    secondaryContainer = Slate850,
    onSecondaryContainer = Slate100,
    tertiary = GermanCrimson,
    onTertiary = PureWhite,
    tertiaryContainer = Slate800,
    onTertiaryContainer = GermanCrimsonLight,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    error = ErrorRose,
    onError = PureWhite
)

private val LightColorScheme = darkColorScheme(
    // DeutschAI defaults to a sleek dark executive mode for immersive German AI tutoring
    primary = GermanGold,
    onPrimary = GermanBlack,
    primaryContainer = Slate800,
    onPrimaryContainer = GermanGoldLight,
    secondary = AIElectricCyan,
    onSecondary = GermanBlack,
    secondaryContainer = Slate850,
    onSecondaryContainer = Slate100,
    tertiary = GermanCrimson,
    onTertiary = PureWhite,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    error = ErrorRose,
    onError = PureWhite
)

@Composable
fun DeutschAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
