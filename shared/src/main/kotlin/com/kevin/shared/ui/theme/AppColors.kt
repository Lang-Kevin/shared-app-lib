package com.kevin.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp

/** Color roles Material 3 has no slot for. Read via AppTheme.colors. */
@Immutable
data class AppColors(
    val accentTint: Color,
    val accentSoft: Color,
    val zones: List<Color>,
    val recording: Color,
    val textDisabled: Color,
    val success: Color,
    val warning: Color,
    val highlight: Color,
    val onMedia: Color,
    val mediaScrim: Color,
    val mediaTextShadow: Shadow,
    val scrimStrong: Color,
    val gridLine: Color,
    val tooltipContainer: Color,
)

private val ZoneMix = listOf(0.10f, 0.28f, 0.50f, 0.75f)

internal fun Brand.toAppColors(): AppColors = AppColors(
    accentTint = lerp(NdTintBase, accent, 0.09f),
    accentSoft = accent.copy(alpha = 0.40f),
    // Z5 is the accent itself, not lerp(…, 1f): lerp goes through Oklab and can drift by one step.
    zones = ZoneMix.map { lerp(NdGraphite, accent, it) } + accent,
    recording = accent,
    textDisabled = NdTextDisabled,
    success = Color(0xFF3FB27F),
    warning = Color(0xFFFFB74D),
    highlight = Color(0xFFFFC850),
    onMedia = Color.White,
    mediaScrim = Color.Black.copy(alpha = 0.25f),
    mediaTextShadow = Shadow(Color.Black.copy(alpha = 0.75f), offset = Offset(0f, 1f), blurRadius = 3f),
    scrimStrong = Color.Black.copy(alpha = 0.78f),
    gridLine = Color.White.copy(alpha = 0.07f),
    tooltipContainer = Color(0xE60B0B0D),
)

internal val LocalAppColors = staticCompositionLocalOf { Brand.Default.toAppColors() }
