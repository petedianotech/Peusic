package com.petediano.peusic.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = AuroraBlue,
    onPrimary = Color.White,
    primaryContainer = IcyBlue,
    onPrimaryContainer = Color(0xFF0C2A6B),
    secondary = MutedTeal,
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = Error,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AuroraBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = IcyBlue,
    secondary = MutedTeal,
    onSecondary = Color(0xFF042F2E),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = Error,
    onError = Color.White
)

@Composable
fun sonicAuroraBrush(alpha: Float = 1f): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            GradientTop.copy(alpha = alpha),
            GradientMid3.copy(alpha = alpha),
            GradientMid2.copy(alpha = alpha * 0.9f),
            GradientMid1.copy(alpha = alpha),
            GradientBottom.copy(alpha = alpha)
        )
    )
}

enum class PeusicThemeMode { SYSTEM, LIGHT, DARK }

@Composable
fun PeusicTheme(
    themeMode: PeusicThemeMode = PeusicThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        PeusicThemeMode.SYSTEM -> isSystemInDarkTheme()
        PeusicThemeMode.LIGHT -> false
        PeusicThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PeusicTypography,
        shapes = PeusicShapes,
        content = content
    )
}
