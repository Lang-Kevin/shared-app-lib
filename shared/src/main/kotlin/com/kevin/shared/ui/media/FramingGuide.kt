package com.kevin.shared.ui.media

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppTheme
import kotlin.math.roundToInt

object FramingGuideDefaults {
    const val IdleAlpha: Float = 1f
    const val RecordingAlpha: Float = 0.55f
    val FootLineWidth: Dp = 1.5.dp
    val BracketWidth: Dp = 2.dp
    val BracketArm: Dp = 24.dp

    /** Spec §7.3 A2: full strength while idle, dimmed to 55% while recording. */
    fun alpha(recording: Boolean, outOfFrame: Boolean = false): Float =
        if (recording && !outOfFrame) RecordingAlpha else IdleAlpha // PO override: out-of-frame stays full

    /** Final mockup: out of frame turns the guide to the error colour; otherwise onMedia. */
    @Composable
    fun color(outOfFrame: Boolean): Color =
        if (outOfFrame) MaterialTheme.colorScheme.error else AppTheme.colors.onMedia
}

internal data class FramingGeometry(
    val footY: Float,
    val roiLeft: Float,
    val roiRight: Float,
    val roiTop: Float,
    val arm: Float,
)

/** Fractions are clamped to 0..1. The bracket arm never exceeds half the ROI width or half its height. */
internal fun framingGeometry(
    width: Float,
    height: Float,
    footLineFraction: Float,
    roiTopFraction: Float,
    roiWidthFraction: Float,
    armPx: Float,
): FramingGeometry {
    val footY = height * footLineFraction.coerceIn(0f, 1f)
    val roiTop = height * roiTopFraction.coerceIn(0f, 1f)
    val roiWidth = width * roiWidthFraction.coerceIn(0f, 1f)
    val roiLeft = (width - roiWidth) / 2f
    val arm = minOf(armPx, roiWidth / 2f, ((footY - roiTop) / 2f).coerceAtLeast(0f))
    return FramingGeometry(footY, roiLeft, roiLeft + roiWidth, roiTop, arm)
}

/** Hint goes [gap] below the foot line; if it would cross [bottomLimit] it flips above the line. */
internal fun framingHintTop(footY: Float, gap: Float, hintHeight: Float, bottomLimit: Float): Float {
    val below = footY + gap
    return if (below + hintHeight <= bottomLimit) below
    else (footY - gap - hintHeight).coerceAtLeast(0f)
}

private fun DrawScope.drawFraming(g: FramingGeometry, color: Color, footStroke: Float, bracketStroke: Float) {
    drawLine(color, start = Offset(0f, g.footY), end = Offset(size.width, g.footY), strokeWidth = footStroke)
    if (g.arm <= 0f) return
    fun bracket(x: Float, y: Float, dx: Float, dy: Float) {
        drawLine(color, start = Offset(x, y), end = Offset(x + dx * g.arm, y), strokeWidth = bracketStroke, cap = StrokeCap.Square)
        drawLine(color, start = Offset(x, y), end = Offset(x, y + dy * g.arm), strokeWidth = bracketStroke, cap = StrokeCap.Square)
    }
    bracket(g.roiLeft, g.roiTop, 1f, 1f)
    bracket(g.roiRight, g.roiTop, -1f, 1f)
    bracket(g.roiLeft, g.footY, 1f, -1f)
    bracket(g.roiRight, g.footY, -1f, -1f)
}

/**
 * Foot line plus ROI corner brackets over the camera preview. Give it the preview's bounds
 * (e.g. `Modifier.matchParentSize()` inside the preview box).
 *
 * - [alpha]: use `FramingGuideDefaults.alpha(recording, outOfFrame)`. It dims the lines only, never the hint.
 * - [color]: use `FramingGuideDefaults.color(outOfFrame)`.
 * - [hint]: optional over-video hint (e.g. a centred `MediaText`), placed under the foot line.
 *   It flips above the line if it would reach [bottomInset] from the bottom. Pass the HUD's
 *   bottom band height there (`CameraHud(onBottomBandHeight = …)`, converted to dp).
 */
@Composable
fun FramingGuide(
    footLineFraction: Float,
    roiTopFraction: Float,
    roiWidthFraction: Float,
    modifier: Modifier = Modifier,
    alpha: Float = FramingGuideDefaults.IdleAlpha,
    color: Color = AppTheme.colors.onMedia,
    bottomInset: Dp = 0.dp,
    hint: (@Composable () -> Unit)? = null,
) {
    val gap = AppTheme.spacing.sm
    val hintSidePadding = AppTheme.spacing.lg
    val guideColor = color.copy(alpha = color.alpha * alpha.coerceIn(0f, 1f))
    Layout(
        modifier = modifier,
        content = {
            Canvas(Modifier.fillMaxSize()) {
                val g = framingGeometry(
                    width = size.width,
                    height = size.height,
                    footLineFraction = footLineFraction,
                    roiTopFraction = roiTopFraction,
                    roiWidthFraction = roiWidthFraction,
                    armPx = FramingGuideDefaults.BracketArm.toPx(),
                )
                drawFraming(g, guideColor, FramingGuideDefaults.FootLineWidth.toPx(), FramingGuideDefaults.BracketWidth.toPx())
            }
            if (hint != null) {
                Box(Modifier.padding(horizontal = hintSidePadding), contentAlignment = Alignment.Center) { hint() }
            }
        },
    ) { measurables, constraints ->
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else constraints.minWidth
        val height = if (constraints.hasBoundedHeight) constraints.maxHeight else constraints.minHeight
        val guide = measurables[0].measure(Constraints.fixed(width, height))
        val hintPlaceable = measurables.getOrNull(1)?.measure(Constraints(maxWidth = width))
        val footY = height * footLineFraction.coerceIn(0f, 1f)
        val bottomLimit = (height - bottomInset.toPx()).coerceAtLeast(0f)
        val gapPx = gap.toPx()
        layout(width, height) {
            guide.place(0, 0)
            if (hintPlaceable != null) {
                val top = framingHintTop(footY, gapPx, hintPlaceable.height.toFloat(), bottomLimit)
                hintPlaceable.place((width - hintPlaceable.width) / 2, top.roundToInt())
            }
        }
    }
}
