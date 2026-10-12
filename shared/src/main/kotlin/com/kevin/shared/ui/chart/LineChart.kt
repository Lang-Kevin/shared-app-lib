package com.kevin.shared.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.max

/**
 * Zone line chart for hr Live and Detail. x is in data units (sample index, or [LineSeries.xs]
 * such as seconds); [window] is the visible x range: Live `LiveWindow.toWindow(size)`, Detail
 * `ChartViewport.toWindow(totalSeconds)`. The plot spans the full width, so gesture px map to
 * fractions with `plotFraction(x, 0f, widthPx)` (zoom anchor, [scrubFraction]).
 *
 * - [zones]: lower-bound line in the zone ramp color + caller label; [targetZone] gets the
 *   accent band and, with [goalLabel], the GOAL pill (spec §7.4: no axis zone strip).
 * - [dynamicScale]: false = zones ± 8 (static), true = visible data ± 10 % incl. target band.
 * - [referenceLine]: dashed accent line (session mean), full width or only over `segments`.
 * - [markers]: dashed highlight verticals with labels (milestones).
 * - [lastPoint]: accent dot on the last finite sample (+ [lastPointLabel] above it).
 * - [scrubFraction] (0..1 of the width): scrubber line, accent dot, [scrubLabel] tooltip.
 * Gaps: non-finite y. All colors via [colors]; all text is caller-supplied.
 */
@Composable
fun LineChart(
    series: LineSeries,
    window: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    zones: List<ChartZone> = emptyList(),
    targetZone: Int? = null,
    goalLabel: String? = null,
    dynamicScale: Boolean = false,
    referenceLine: ChartReferenceLine? = null,
    markers: List<ChartMarker> = emptyList(),
    lastPoint: Boolean = false,
    lastPointLabel: ((y: Float) -> String)? = null,
    scrubFraction: Float? = null,
    scrubLabel: ((x: Float, y: Float) -> String)? = null,
    timeAxis: Boolean = true,
    xLabel: (x: Float) -> String = { formatTickLabel(it) },
    contentDescription: String? = null,
    colors: ChartColors = ChartDefaults.colors(),
    maxPoints: Int = ChartDefaults.MaxPoints,
) {
    val measurer = rememberTextMeasurer()
    val labelStyle = ChartDefaults.labelStyle()
    val numberStyle = ChartDefaults.numberStyle()
    val a11y = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Canvas(modifier.then(a11y)) {
        val w = size.width
        if (w <= 0f || size.height <= 0f) return@Canvas
        val top = ChartDefaults.TopInset.toPx()
        val bottom = max(top + 1f, size.height - if (timeAxis) ChartDefaults.AxisHeight.toPx() else 0f)
        val plotH = bottom - top
        val thin = ChartDefaults.ThinLineWidth.toPx()
        val inset = ChartDefaults.LabelInset.toPx()
        val gap = ChartDefaults.LabelGap.toPx()
        val dotR = ChartDefaults.DotRadius.toPx()
        val padH = ChartDefaults.PillPaddingH.toPx()
        val padV = ChartDefaults.PillPaddingV.toPx()

        val visible = visibleIndices(series.size, window.start, window.endInclusive, series::xAt)
        val points = decimate(visible, maxPoints, series::xAt, series::yAt)
        val yRange = zoneChartRange(points.map { it.y }, zones, targetZone, dynamicScale)
        fun px(x: Float) = xToPx(x, window, 0f, w)
        fun py(y: Float) = yToPx(y, yRange, top, plotH)

        // Target band
        val target = zones.firstOrNull { it.number == targetZone }
        if (target != null) {
            val bandTop = py(target.hi).coerceIn(top, bottom)
            val bandBottom = py(target.lo).coerceIn(top, bottom)
            if (bandBottom > bandTop) drawRect(colors.targetBand, Offset(0f, bandTop), Size(w, bandBottom - bandTop))
        }

        // Zone lower-bound lines + labels
        for (z in zones) {
            val y = py(z.lo)
            if (y < top || y > bottom) continue
            drawHLine(y, 0f, w, colors.zoneColor(z.number), thin)
            val layout = measurer.measure(z.label, numberStyle)
            val c = if (z.number == targetZone) colors.targetZoneLabel else colors.zoneLabel
            drawText(layout, c, Offset(inset, (y - layout.size.height - gap).coerceAtLeast(0f)))
        }

        // GOAL badge, top-left inside the target band
        if (target != null && goalLabel != null) {
            val bandTop = py(target.hi)
            if (bandTop in top..bottom || py(target.lo) in top..bottom) {
                val layout = measurer.measure(goalLabel, labelStyle)
                val pill = pillSize(layout, padH, padV)
                val pillTop = (bandTop.coerceAtLeast(top) + gap).coerceAtMost(max(top, bottom - pill.height))
                drawPill(layout, Offset(inset, pillTop), padH, padV, colors.goalFill, colors.goalText, colors.goalBorder, thin)
            }
        }

        // Markers (milestones)
        val markerDash = ChartDefaults.MarkerDash.toPx()
        for (m in markers) {
            if (m.x < window.start || m.x > window.endInclusive) continue
            val x = px(m.x)
            drawVLine(x, top, bottom, colors.markerLine, thin, floatArrayOf(markerDash, markerDash))
            val layout = measurer.measure(m.label, numberStyle)
            val left = (x + gap).coerceIn(0f, max(0f, w - layout.size.width))
            drawText(layout, colors.marker, Offset(left, (top - layout.size.height).coerceAtLeast(0f)))
        }

        // Reference (mean) line
        if (referenceLine != null && referenceLine.value.isFinite()) {
            val y = py(referenceLine.value)
            if (y in top..bottom) {
                val dash = floatArrayOf(ChartDefaults.DashOn.toPx(), ChartDefaults.DashOff.toPx())
                val segments = referenceLine.segments
                if (segments == null) {
                    drawHLine(y, 0f, w, colors.accent, thin, dash)
                } else {
                    for (s in segments) {
                        val a = px(s.start).coerceIn(0f, w)
                        val b = px(s.endInclusive).coerceIn(0f, w)
                        if (b > a) drawHLine(y, a, b, colors.accent, thin, dash)
                    }
                }
                referenceLine.label?.let { label ->
                    val layout = measurer.measure(label, numberStyle)
                    val left = max(0f, w - layout.size.width - inset)
                    drawText(layout, colors.accent, Offset(left, (y - layout.size.height - gap).coerceAtLeast(0f)))
                }
            }
        }

        // Data line
        drawPolyline(
            points.map { Offset(px(it.x), if (it.y.isFinite()) py(it.y) else Float.NaN) },
            colors.line,
            ChartDefaults.LineWidth.toPx(),
        )

        // Last point
        if (lastPoint) {
            val li = lastFiniteIndex(series.size, series::yAt)
            if (li != null) {
                val lx = series.xAt(li)
                val ly = series.yAt(li)
                if (lx >= window.start && lx <= window.endInclusive) {
                    val c = Offset(px(lx), py(ly))
                    drawCircle(colors.accent, dotR, c)
                    lastPointLabel?.let { f ->
                        val layout = measurer.measure(f(ly), numberStyle)
                        val left = (c.x - layout.size.width).coerceIn(0f, max(0f, w - layout.size.width))
                        drawText(layout, colors.line, Offset(left, (c.y - dotR - gap - layout.size.height).coerceAtLeast(0f)))
                    }
                }
            }
        }

        // Time axis
        if (timeAxis) {
            for (t in timeTicks(window.start, window.endInclusive, ChartDefaults.MaxTimeTicks)) {
                val layout = measurer.measure(xLabel(t), numberStyle)
                val left = (px(t) - layout.size.width / 2f).coerceIn(0f, max(0f, w - layout.size.width))
                drawText(layout, colors.axisLabel, Offset(left, bottom + gap))
            }
        }

        // Scrubber + tooltip
        if (scrubFraction != null && !scrubFraction.isNaN()) {
            val idx = nearestIndexByX(series.size, fractionToX(scrubFraction, window), series::xAt)
            if (idx != null) {
                val sx = series.xAt(idx)
                val sy = series.yAt(idx)
                val x = px(sx)
                drawVLine(x, top, bottom, colors.scrubLine, thin)
                if (sy.isFinite()) {
                    drawCircle(colors.accent, dotR, Offset(x, py(sy)))
                    scrubLabel?.let { f ->
                        val layout = measurer.measure(f(sx, sy), numberStyle)
                        val pill = pillSize(layout, padH, padV)
                        val left = tooltipLeft(x, pill.width, 0f, w)
                        drawPill(layout, Offset(left, 0f), padH, padV, colors.tooltipFill, colors.tooltipText, colors.tooltipBorder, thin)
                    }
                }
            }
        }
    }
}
