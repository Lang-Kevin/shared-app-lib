package com.kevin.shared.ui.chart

import kotlin.math.abs

/**
 * Position of [xPx] in a plot starting at [left] with [width] px, as 0..1 (clamped); 0 for an
 * empty plot or NaN. Charts here draw edge to edge, so apps pass `plotFraction(x, 0f, widthPx)`
 * as zoom anchor and scrub fraction (hr `LiveScreen.kt:256-293` used a left gutter).
 */
fun plotFraction(xPx: Float, left: Float, width: Float): Float {
    if (!(width > 0f) || xPx.isNaN()) return 0f
    return ((xPx - left) / width).coerceIn(0f, 1f)
}

/** Data x to px; a zero-span window maps to the plot center. */
internal fun xToPx(x: Float, window: ClosedFloatingPointRange<Float>, left: Float, width: Float): Float {
    val span = window.endInclusive - window.start
    if (!(span > 0f)) return left + width / 2f
    return left + (x - window.start) / span * width
}

/** Data y to px (up = larger); a flat range maps to the vertical center. */
internal fun yToPx(y: Float, range: ClosedFloatingPointRange<Float>, top: Float, height: Float): Float {
    val span = range.endInclusive - range.start
    if (!(span > 0f)) return top + height / 2f
    return top + (1f - (y - range.start) / span) * height
}

/** Plot fraction (clamped 0..1) to data x inside [window]. */
internal fun fractionToX(fraction: Float, window: ClosedFloatingPointRange<Float>): Float =
    window.start + fraction.coerceIn(0f, 1f) * (window.endInclusive - window.start)

/** Index whose x is nearest to [x] (x ascending, binary search); ties pick the earlier sample. */
internal fun nearestIndexByX(size: Int, x: Float, xAt: (Int) -> Float): Int? {
    if (size <= 0 || x.isNaN()) return null
    var lo = 0
    var hi = size - 1
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (xAt(mid) < x) lo = mid + 1 else hi = mid
    }
    if (lo > 0 && abs(xAt(lo - 1) - x) <= abs(xAt(lo) - x)) return lo - 1
    return lo
}

/** Left edge of a tooltip of [width] centered on [anchorX], kept inside [minX]..[maxX]. */
internal fun tooltipLeft(anchorX: Float, width: Float, minX: Float, maxX: Float): Float {
    val maxLeft = maxX - width
    if (maxLeft <= minX) return minX
    return (anchorX - width / 2f).coerceIn(minX, maxLeft)
}

/** x of trend point [i] of [n] across [left]..[left]+[width]; a single point is centered. */
internal fun trendX(i: Int, n: Int, left: Float, width: Float): Float =
    if (n <= 1) left + width / 2f else left + i * width / (n - 1)

/** Horizontal segment of a stacked bar: [index] into the input list. */
internal data class Segment(val index: Int, val left: Float, val width: Float)

/**
 * Stacked time-in-zone segments: zones with duration <= 0 are skipped, [gap] only between drawn
 * segments (dropped if it doesn't fit), the last segment ends exactly at [width].
 */
internal fun zoneSegments(durations: List<Long>, width: Float, gap: Float): List<Segment> {
    val drawn = durations.indices.filter { durations[it] > 0L }
    if (drawn.isEmpty() || !(width > 0f)) return emptyList()
    val total = drawn.sumOf { durations[it].toDouble() }
    val wantGap = gap.coerceAtLeast(0f)
    val g = if (width - wantGap * (drawn.size - 1) > 0f) wantGap else 0f
    val avail = width - g * (drawn.size - 1)
    val out = ArrayList<Segment>(drawn.size)
    var left = 0f
    drawn.forEachIndexed { k, idx ->
        val w = if (k == drawn.lastIndex) width - left
        else (avail * durations[idx].toDouble() / total).toFloat()
        out += Segment(idx, left, w)
        left += w + g
    }
    return out
}

/** Bar rectangle in px, origin top-left of the plot. */
internal data class BarRect(val left: Float, val top: Float, val width: Float, val height: Float)

/**
 * Equal-width bars over [width] with [gap] between them (dropped if it doesn't fit), heights
 * relative to the largest finite positive value; zero/negative/NaN and an all-zero set give 0.
 */
internal fun barGeometry(values: List<Float>, width: Float, height: Float, gap: Float): List<BarRect> {
    val n = values.size
    if (n == 0 || !(width > 0f)) return emptyList()
    var g = gap.coerceAtLeast(0f)
    var barW = (width - g * (n - 1)) / n
    if (barW <= 0f) {
        g = 0f
        barW = width / n
    }
    var max = 0f
    for (v in values) if (v.isFinite() && v > max) max = v
    return values.mapIndexed { i, v ->
        val h = if (max > 0f && v.isFinite() && v > 0f) v / max * height else 0f
        BarRect(i * (barW + g), height - h, barW, h)
    }
}
