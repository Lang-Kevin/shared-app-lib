package com.kevin.shared.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlin.math.max

/**
 * Vertical bars (Stats weekly load, mockup 206-215): outlineVariant bars with 3dp top radius,
 * [highlightIndex] (e.g. the current week) in accentTint with a 1dp accent border. Heights are
 * relative to the largest value; zero/negative/NaN draw nothing. [labels] (one per bar, up to
 * [maxLabels] shown, anchored at the last) sit below; the highlighted label is onSurface.
 */
@Composable
fun BarChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    labels: List<String> = emptyList(),
    maxLabels: Int = Int.MAX_VALUE,
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
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val labelH = if (labels.isNotEmpty()) ChartDefaults.AxisHeight.toPx() else 0f
        val rects = barGeometry(values, size.width, max(0f, size.height - labelH), ChartDefaults.BarGap.toPx())
        val radius = ChartDefaults.BarCorner.toPx()
        val border = ChartDefaults.ThinLineWidth.toPx()
        rects.forEachIndexed { i, rect ->
            if (i == highlightIndex) {
                drawTopRoundedRect(rect, radius, colors.barHighlight)
                val half = border / 2f
                val inner = BarRect(rect.left + half, rect.top + half, rect.width - border, rect.height - half)
                drawTopRoundedRect(inner, radius, colors.barHighlightBorder, Stroke(border))
            } else {
                drawTopRoundedRect(rect, radius, colors.bar)
            }
        }
        for (i in labelIndices(labels.size, maxLabels)) {
            if (i >= rects.size) continue
            val rect = rects[i]
            val layout = measurer.measure(labels[i], numberStyle)
            val color = if (i == highlightIndex) colors.barLabelHighlight else colors.barLabel
            val lx = (rect.left + rect.width / 2f - layout.size.width / 2f)
                .coerceIn(0f, max(0f, size.width - layout.size.width))
            drawText(layout, color, Offset(lx, size.height - layout.size.height))
        }
    }
}
