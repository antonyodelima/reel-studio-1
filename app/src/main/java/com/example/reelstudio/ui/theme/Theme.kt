package com.example.reelstudio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = PaperCard,
    primaryContainer = CoralLight,
    onPrimaryContainer = Coral,
    secondary = Violet,
    onSecondary = PaperCard,
    secondaryContainer = VioletLight,
    onSecondaryContainer = Violet,
    tertiary = Mint,
    onTertiary = PaperCard,
    background = Paper,
    onBackground = Ink,
    surface = PaperCard,
    onSurface = Ink,
    surfaceVariant = Paper,
    onSurfaceVariant = Muted,
    outline = BorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Coral,
    onPrimary = PaperCard,
    primaryContainer = DarkSurface,
    onPrimaryContainer = Coral,
    secondary = Violet,
    onSecondary = PaperCard,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = Violet,
    tertiary = Mint,
    onTertiary = PaperCard,
    background = PaperDark,
    onBackground = Paper,
    surface = DarkSurface,
    onSurface = Paper,
    surfaceVariant = Ink,
    onSurfaceVariant = Muted,
    outline = BorderLight
)

@Composable
fun ReelStudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
