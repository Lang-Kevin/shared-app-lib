package com.kevin.shared.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppTheme

/** Shutter geometry (mockup 02 ratios, jump's 72dp size kept). */
object ShutterDefaults {
    val Size: Dp = 72.dp
    val RingWidth: Dp = 4.dp
    val IdleDiscSize: Dp = 56.dp
    val StopSquareSize: Dp = 24.dp
    val ProgressWidth: Dp = 4.dp
    const val DisabledAlpha: Float = 0.4f
}

internal fun shutterGlyphSize(recording: Boolean): Dp =
    if (recording) ShutterDefaults.StopSquareSize else ShutterDefaults.IdleDiscSize

/** Arc sweep in degrees, or null when no arc is drawn (idle, or no progress, or NaN). */
internal fun shutterSweep(recording: Boolean, progress: Float?): Float? =
    if (!recording || progress == null || progress.isNaN()) null
    else 360f * progress.coerceIn(0f, 1f)

/**
 * Record / stop button on video. Idle: white disc in a white ring. Recording: small square in
 * the recording colour. [progress] (0..1) draws an arc over the ring for capped modes; it is
 * not a timer, so it is optional and off by default (§7.3 A2).
 *
 * [contentDescription] is the app's localized action, e.g. "Start recording" / "Stop recording".
 * The button holds no state: the app debounces double taps and decides what [onClick] does.
 */
@Composable
fun ShutterButton(
    recording: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    progress: Float? = null,
) {
    val colors = AppTheme.colors
    val sweep = shutterSweep(recording, progress)
    Box(
        modifier = modifier
            .size(ShutterDefaults.Size)
            .alpha(if (enabled) 1f else ShutterDefaults.DisabledAlpha)
            .drawWithContent {
                drawContent()
                if (sweep != null) {
                    val stroke = ShutterDefaults.ProgressWidth.toPx()
                    val inset = stroke / 2f
                    drawArc(
                        color = colors.recording,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = Size(size.width - stroke, size.height - stroke),
                        style = Stroke(width = stroke),
                    )
                }
            }
            .border(ShutterDefaults.RingWidth, colors.onMedia, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(shutterGlyphSize(recording))
                .background(
                    color = if (recording) colors.recording else colors.onMedia,
                    shape = if (recording) MaterialTheme.shapes.extraSmall else CircleShape,
                ),
        )
    }
}
