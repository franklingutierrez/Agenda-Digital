package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PastelLavenderDark,
    onPrimary = Color(0xFF1E1730),
    primaryContainer = Color(0xFF382F52),
    onPrimaryContainer = PastelLavenderContainer,
    secondary = PastelMint,
    onSecondary = Color(0xFF00391A),
    secondaryContainer = Color(0xFF1E3A2B),
    onSecondaryContainer = PastelMintContainer,
    tertiary = PastelPeach,
    onTertiary = Color(0xFF492000),
    tertiaryContainer = Color(0xFF4C2B1B),
    onTertiaryContainer = PastelPeachContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF2E3242)
)

private val LightColorScheme = lightColorScheme(
    primary = PastelLavenderPrimary,
    onPrimary = Color.White,
    primaryContainer = PastelLavenderContainer,
    onPrimaryContainer = Color(0xFF261845),
    secondary = PastelMint,
    onSecondary = Color.White,
    secondaryContainer = PastelMintContainer,
    onSecondaryContainer = PastelMintText,
    tertiary = PastelPeach,
    onTertiary = Color.White,
    tertiaryContainer = PastelPeachContainer,
    onTertiaryContainer = PastelPeachText,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = Color(0xFFECEAF4)
)

@Composable
fun AgendaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
