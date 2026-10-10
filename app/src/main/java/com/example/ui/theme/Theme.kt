package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.preferences.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = CoolBrandCrimson,
    onPrimary = Color.White,
    primaryContainer = CoolGlassSurfaceElevated,
    onPrimaryContainer = CoolBrandCrimsonLight,
    secondary = PrismaticCyan,
    onSecondary = CoolObsidianBase,
    tertiary = CausticAmber,
    background = CoolObsidianBase,
    onBackground = TextPrimaryDark,
    surface = CoolGlassSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF1B202C),
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorderSubtleDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = CoolBrandCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F5F9),
    onPrimaryContainer = CoolBrandCrimson,
    secondary = PrismaticCyan,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = TextPrimaryLight,
    surface = CoolGlassSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextSecondaryLight,
    outline = GlassBorderSubtleLight,
    error = ErrorRed
)

@Composable
fun XiaoxingBrowserTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
