package com.example.louarayamacrame.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleSurface,
    onPrimaryContainer = PurpleDark,
    secondary = TerracottaPrimary,
    onSecondary = Color.White,
    secondaryContainer = CreamSurface,
    onSecondaryContainer = TerracottaDark,
    tertiary = SageGreen,
    onTertiary = Color.White,
    tertiaryContainer = SageGreenLight,
    onTertiaryContainer = CharcoalText,
    background = CreamBackground,
    onBackground = CharcoalText,
    surface = CreamCard,
    onSurface = CharcoalText,
    surfaceVariant = CreamSurface,
    onSurfaceVariant = MutedText,
    outline = PurpleBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = PurpleLight,
    onPrimary = CharcoalText,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleSurface,
    secondary = TerracottaLight,
    onSecondary = CharcoalText,
    background = CharcoalText,
    onBackground = CreamBackground,
    surface = Color(0xFF282030),
    onSurface = CreamBackground
)

@Composable
fun LouArayaMacrameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
