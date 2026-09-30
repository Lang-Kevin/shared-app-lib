package com.kevin.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimary,
    secondary = SecondaryBlue,
    tertiary = TertiaryPink,
    error = ErrorRed,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = LightPurple,
)

/** The one app theme: dark scheme from SharedColors (incl. onSurface = LightPurple) + AppTypography. */
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
