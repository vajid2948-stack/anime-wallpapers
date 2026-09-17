package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AnimeColorScheme = darkColorScheme(
    primary = AnimeNeonPink,
    onPrimary = TextPrimary,
    primaryContainer = AnimeDarkSurfaceHighlight,
    onPrimaryContainer = AnimeNeonPink,
    secondary = AnimeNeonCyan,
    onSecondary = AnimeDarkVoid,
    secondaryContainer = AnimeDarkSurfaceVariant,
    onSecondaryContainer = AnimeNeonCyan,
    tertiary = AnimeNeonPurple,
    onTertiary = TextPrimary,
    background = AnimeDarkVoid,
    onBackground = TextPrimary,
    surface = AnimeDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = AnimeDarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AnimeColorScheme,
        typography = Typography,
        content = content
    )
}
