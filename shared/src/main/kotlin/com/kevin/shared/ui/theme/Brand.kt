package com.kevin.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Night Drive default accent. */
val NightDriveRed = Color(0xFFE8202E)

/**
 * The only per-app theme input. Everything accent-dependent (scheme primary/tertiary,
 * accentTint, zone ramp, recording) is derived from [accent]; see toColorScheme()/toAppColors().
 */
@Immutable
data class Brand(
    val accent: Color = NightDriveRed,
    val onAccent: Color = Color(0xFFFFFFFF),
) {
    companion object {
        val Default = Brand()
    }
}

// Night Drive neutrals (spec §4.3). Internal: apps read them through MaterialTheme / AppTheme.colors.
internal val NdBackground = Color(0xFF000000)
internal val NdSurface = Color(0xFF0B0B0D)
internal val NdOutline = Color(0xFF1F1F23)
internal val NdOutlineVariant = Color(0xFF2A2A2F)
internal val NdOnSurface = Color(0xFFF2F2F4)
internal val NdOnSurfaceVariant = Color(0xFF8D8D94)
internal val NdTextDisabled = Color(0xFF55555C)
internal val NdSecondary = Color(0xFFA3A3AB)
internal val NdError = Color(0xFFFF5A4F)
internal val NdGraphite = Color(0xFF26262B)   // zone ramp start
internal val NdTintBase = Color(0xFF0A0A0A)   // accentTint base
