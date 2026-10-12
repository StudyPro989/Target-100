package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.model.AccentTheme
import com.example.model.AppThemeMode

@Composable
fun Target100Theme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentTheme: AccentTheme = AccentTheme.GOLD_RAJPUTANA,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val (primary, primaryContainer, onPrimaryContainer) = when (accentTheme) {
        AccentTheme.GOLD_RAJPUTANA -> Triple(GoldPrimary, GoldPrimaryContainer, GoldOnPrimaryContainer)
        AccentTheme.MARWAR_BLUE -> Triple(BluePrimary, BluePrimaryContainer, BlueOnPrimaryContainer)
        AccentTheme.JAIPUR_EMERALD -> Triple(GreenPrimary, GreenPrimaryContainer, GreenOnPrimaryContainer)
        AccentTheme.MEWAR_PURPLE -> Triple(PurplePrimary, PurplePrimaryContainer, PurpleOnPrimaryContainer)
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer.copy(alpha = 0.25f),
            onPrimaryContainer = Color.White,
            secondary = primary.copy(alpha = 0.8f),
            onSecondary = Color.White,
            background = BackgroundDark,
            surface = SurfaceDark,
            surfaceVariant = CardDark,
            onBackground = TextPrimaryDark,
            onSurface = TextPrimaryDark,
            onSurfaceVariant = TextSecondaryDark,
            error = StatusError
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = primary.copy(alpha = 0.85f),
            onSecondary = Color.White,
            background = BackgroundLight,
            surface = SurfaceLight,
            surfaceVariant = CardLight,
            onBackground = TextPrimaryLight,
            onSurface = TextPrimaryLight,
            onSurfaceVariant = TextSecondaryLight,
            error = StatusError
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
