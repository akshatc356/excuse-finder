package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ExcuseDarkColorScheme = darkColorScheme(
    primary = AccentYellow,
    onPrimary = Color(0xFF000000),
    primaryContainer = AccentYellowMuted,
    onPrimaryContainer = AccentYellow,
    secondary = AccentYellow,
    onSecondary = Color(0xFF000000),
    secondaryContainer = CardBgElevated,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentCyan,
    onTertiary = Color(0xFF000000),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = CardBg,
    onSurface = TextPrimary,
    surfaceVariant = CardBgElevated,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = Color(0xFF333333),
    error = WarningRed,
    onError = Color(0xFFFFFFFF),
    errorContainer = WarningRedBg,
    onErrorContainer = WarningRed
)

@Composable
fun ExcuseFinderTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ExcuseDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Ensure edge-to-edge dark theme status and navigation bars
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
