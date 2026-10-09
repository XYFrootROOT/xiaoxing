package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.preferences.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonFlame,
    onPrimary = Color.White,
    primaryContainer = ObsidianCard,
    onPrimaryContainer = RubyAccent,
    secondary = SakuraBlossom,
    onSecondary = DarkCherryNight,
    tertiary = GoldenAmber,
    background = DarkCherryNight,
    onBackground = TextPrimaryDark,
    surface = ObsidianCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF261523),
    onSurfaceVariant = TextSecondaryDark,
    outline = CrimsonBorder,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonFlame,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE5EC),
    onPrimaryContainer = CrimsonFlame,
    secondary = RubyAccent,
    onSecondary = Color.White,
    background = SakuraLightBg,
    onBackground = SakuraLightTextPrimary,
    surface = SakuraLightSurface,
    onSurface = SakuraLightTextPrimary,
    surfaceVariant = Color(0xFFFFE8EE),
    onSurfaceVariant = Color(0xFF7A4356),
    outline = SakuraLightBorder,
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
