package com.kevin.shared.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppTheme

/** §7.3 A2: scrim over the bottom 20% only. */
internal const val HudScrimHeightFraction = 0.20f

/** §7.3 A2: scrim ≤ 25%. The token mediaScrim is 0.25 (§4.4); the HUD still clamps defensively. */
internal const val HudScrimMaxAlpha = 0.25f

internal val HudTouchTarget: Dp = 48.dp

internal fun hudScrimPeakAlpha(tokenAlpha: Float): Float = minOf(tokenAlpha, HudScrimMaxAlpha)

internal data class HudSlots(val idleControls: Boolean, val readings: Boolean)

/** Idle shows the camera controls; recording shows only the readings (§7.3 A2). Never both. */
internal fun hudSlots(recording: Boolean): HudSlots =
    HudSlots(idleControls = !recording, readings = recording)

/**
 * Camera chrome in the A2 layout (spec §7.3), drawn over a full-bleed preview.
 *
 * - Top: [navigation] at the start; [phase] centred, uppercase, on the video with a shadow (no pill).
 * - Bottom: start flank, shutter, end flank. The flanks share the width equally, so the shutter
 *   stays centred whatever the flanks contain (including an empty or placeholder reading).
 *   Idle shows [idleStart]/[idleEnd]; recording shows [recordingStart]/[recordingEnd]. The swap
 *   is instant, so a hidden control is never tappable mid-animation.
 * - Scrim: gradient to ≤25% over the bottom 20%, full-bleed. [contentInsets] apply to the
 *   controls, not to the scrim.
 * - [onBottomBandHeight] reports the bottom band's height in px (including insets) so the app can
 *   keep the foot line and the framing hint clear of it (`FramingGuide(bottomInset = …)`).
 *
 * Stateless. The app owns recording state, phase strings (localized) and shutter debouncing.
 */
@Composable
fun CameraHud(
    recording: Boolean,
    modifier: Modifier = Modifier,
    phase: String? = null,
    phaseStyle: TextStyle = MaterialTheme.typography.titleMedium,
    contentInsets: WindowInsets = WindowInsets.safeDrawing,
    onBottomBandHeight: (Int) -> Unit = {},
    navigation: @Composable () -> Unit = {},
    idleStart: @Composable () -> Unit = {},
    idleEnd: @Composable () -> Unit = {},
    recordingStart: @Composable () -> Unit = {},
    recordingEnd: @Composable () -> Unit = {},
    shutter: @Composable () -> Unit,
) {
    val colors = AppTheme.colors
    val spacing = AppTheme.spacing
    val slots = hudSlots(recording)
    val scrimPeak = colors.mediaScrim.copy(alpha = hudScrimPeakAlpha(colors.mediaScrim.alpha))
    Box(modifier.fillMaxSize()) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(HudScrimHeightFraction)
                .background(Brush.verticalGradient(listOf(Color.Transparent, scrimPeak))),
        )
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(contentInsets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .padding(spacing.sm)
                .heightIn(min = HudTouchTarget),
        ) {
            Box(Modifier.align(Alignment.CenterStart)) { navigation() }
            if (phase != null) {
                MediaText(
                    text = phase.uppercase(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = HudTouchTarget),
                    style = phaseStyle,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { onBottomBandHeight(it.height) }
                .windowInsetsPadding(contentInsets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(horizontal = spacing.lg, vertical = spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (slots.idleControls) idleStart()
                if (slots.readings) recordingStart()
            }
            Box(Modifier.padding(horizontal = spacing.md)) { shutter() }
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                if (slots.idleControls) idleEnd()
                if (slots.readings) recordingEnd()
            }
        }
    }
}
