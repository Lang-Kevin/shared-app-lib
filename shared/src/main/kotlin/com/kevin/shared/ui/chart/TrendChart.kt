package com.kevin.shared.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.max

/** One trend line (hr `TrendChart.kt:17-22`); non-finite values are gaps. */
data class TrendSeries(
    val values: List<Float>,
    val color: Color? = null,
    val lines: Boolean = true,
    val dots: Boolean = false,
    val highlightLast: Boolean = false,
)

/**
 * Index-based trend chart (Stats ACWR, Form sparkline, History trend cards). All series share
 * the x positions 0..count-1 (count = longest series or [xLabels] size); a single point is centered.
 * [band] draws a neutral band with optional [bandLabel] at its top-left; [referenceY] a dashed
 * reference line; [xLabels] up to [maxXLabels] category labels anchored at the last item.
 */
@Composable
fun TrendChart(
    series: List<TrendSeries>,
    modifier: Modifier = Modifier,
    band: ClosedFloatingPointRange<Float>? = null,
    bandLabel: String? = null,
    referenceY: Float? = null,
    xLabels: List<String> = emptyList(),
    maxXLabels: Int = 4,
    contentDescription: String? = null,
    colors: ChartColors = ChartDefaults.colors(),
) {
    val measurer = rememberTextMeasurer()
    val numberStyle = ChartDefaults.numberStyle()
    val a11y = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Canvas(modifier.then(a11y)) {
        val w = size.width
        if (w <= 0f || size.height <= 0f) return@Canvas
        val count = max(series.maxOfOrNull { it.values.size } ?: 0, xLabels.size)
        val dotR = ChartDefaults.TrendDotRadius.toPx()
        val hlR = ChartDefaults.DotRadius.toPx()
        val gap = ChartDefaults.LabelGap.toPx()
        val axisH = if (xLabels.isNotEmpty()) ChartDefaults.AxisHeight.toPx() else 0f
        val top = hlR
        val bottom = max(top + 1f, size.height - axisH - hlR)
        val left = hlR
        val plotW = max(1f, w - 2f * hlR)
        val range = trendRange(series.flatMap { it.values }, band, referenceY)
        fun py(v: Float) = yToPx(v, range, top, bottom - top)

        if (band != null) {
            val bandTop = py(band.endInclusive).coerceIn(top, bottom)
            val bandBottom = py(band.start).coerceIn(top, bottom)
            if (bandBottom > bandTop) drawRect(colors.neutralBand, Offset(0f, bandTop), Size(w, bandBottom - bandTop))
            bandLabel?.let { text ->
                val layout = measurer.measure(text, numberStyle)
                val y = (bandTop + gap).coerceAtMost(max(0f, bottom - layout.size.height))
                drawText(layout, colors.bandLabel, Offset(ChartDefaults.LabelInset.toPx(), y))
            }
        }

        if (referenceY != null && referenceY.isFinite()) {
            val dash = floatArrayOf(ChartDefaults.DashOn.toPx(), ChartDefaults.DashOff.toPx())
            drawHLine(py(referenceY), 0f, w, colors.secondaryLine, ChartDefaults.ThinLineWidth.toPx(), dash)
        }

        for (s in series) {
            val color = s.color ?: colors.line
            val pts = s.values.mapIndexed { i, v ->
                Offset(trendX(i, count, left, plotW), if (v.isFinite()) py(v) else Float.NaN)
            }
            if (s.lines) drawPolyline(pts, color, ChartDefaults.LineWidth.toPx())
            if (s.dots) pts.forEach { if (!it.y.isNaN()) drawCircle(color, dotR, it) }
            if (s.highlightLast) {
                lastFiniteIndex(s.values.size) { s.values[it] }?.let { drawCircle(colors.accent, hlR, pts[it]) }
            }
        }

        for (i in labelIndices(xLabels.size, maxXLabels)) {
            val layout = measurer.measure(xLabels[i], numberStyle)
            val cx = trendX(i, count, left, plotW)
            val lx = (cx - layout.size.width / 2f).coerceIn(0f, max(0f, w - layout.size.width))
            drawText(layout, colors.axisLabel, Offset(lx, size.height - layout.size.height))
        }
    }
}
