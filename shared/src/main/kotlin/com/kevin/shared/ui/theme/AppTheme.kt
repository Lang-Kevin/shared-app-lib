package com.kevin.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp

/** Every slot is set: unset slots would keep M3's purple baseline (dialogs, chips, snackbars). */
internal fun Brand.toColorScheme(): ColorScheme {
    val tint = lerp(NdTintBase, accent, 0.09f)
    return darkColorScheme(
        primary = accent, onPrimary = onAccent,
        primaryContainer = tint, onPrimaryContainer = NdOnSurface,
        inversePrimary = accent,
        secondary = NdSecondary, onSecondary = NdBackground,
        secondaryContainer = NdOutlineVariant, onSecondaryContainer = NdOnSurface,
        tertiary = accent, onTertiary = onAccent,
        tertiaryContainer = tint, onTertiaryContainer = NdOnSurface,
        background = NdBackground, onBackground = NdOnSurface,
        surface = NdSurface, onSurface = NdOnSurface,
        surfaceVariant = NdOutline, onSurfaceVariant = NdOnSurfaceVariant,
        surfaceTint = Color.Transparent,
        inverseSurface = NdOnSurface, inverseOnSurface = NdSurface,
        error = NdError, onError = NdBackground,
        errorContainer = lerp(NdTintBase, NdError, 0.12f), onErrorContainer = NdOnSurface,
        outline = NdOutline, outlineVariant = NdOutlineVariant,
        scrim = Color.Black,
        surfaceBright = NdOutline, surfaceDim = NdBackground,
        surfaceContainerLowest = NdBackground, surfaceContainerLow = NdSurface,
        surfaceContainer = NdSurface, surfaceContainerHigh = Color(0xFF141417),
        surfaceContainerHighest = NdOutline,
    )
}

/** The one app theme (Night Drive, dark only). Apps may pass their own accent via [brand]. */
@Composable
fun AppTheme(brand: Brand = Brand.Default, content: @Composable () -> Unit) {
    val appColors = remember(brand) { brand.toAppColors() }
    val colorScheme = remember(brand) { brand.toColorScheme() }
    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppSpacing provides AppSpacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Token accessors for roles M3 lacks: AppTheme.colors / .spacing / .pillShape (MaterialTheme pattern). */
object AppTheme {
    val colors: AppColors
        @Composable @ReadOnlyComposable get() = LocalAppColors.current
    val spacing: AppSpacing
        @Composable @ReadOnlyComposable get() = LocalAppSpacing.current
    val pillShape: Shape get() = PillShape
}
