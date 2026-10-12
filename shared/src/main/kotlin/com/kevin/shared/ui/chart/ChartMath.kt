package com.kevin.shared.ui.chart

import kotlin.math.abs
import kotlin.math.max

/** Static zone scale: this far below the lowest and above the highest zone bound (hr: 8 bpm). */
internal const val ZONE_SCALE_PAD: Float = 8f

/**
 * Y range of the zone chart (hr `TrainingUi.kt:92-108`, `LiveScreen.kt:445-466`).
 * - static ([dynamic] false) with zones: lowest lo − [zonePad] .. highest hi + [zonePad], widened to
 *   include finite data;
 * - otherwise finite values plus the target zone's bounds, padded by max(10 % of span, 1);
 * - nothing finite and no target: 0..1.
 */
internal fun zoneChartRange(
    visibleYs: List<Float>,
    zones: List<ChartZone>,
    targetZone: Int?,
    dynamic: Boolean,
    zonePad: Float = ZONE_SCALE_PAD,
): ClosedFloatingPointRange<Float> {
    var lo = Float.POSITIVE_INFINITY
    var hi = Float.NEGATIVE_INFINITY
    for (y in visibleYs) if (y.isFinite()) {
        if (y < lo) lo = y
        if (y > hi) hi = y
    }
    if (!dynamic && zones.isNotEmpty()) {
        return minOf(zones.minOf { it.lo } - zonePad, lo)..maxOf(zones.maxOf { it.hi } + zonePad, hi)
    }
    val target = zones.firstOrNull { it.number == targetZone }
    if (target != null) {
        lo = minOf(lo, target.lo)
        hi = maxOf(hi, target.hi)
    }
    if (lo > hi) return 0f..1f
    val pad = max((hi - lo) * 0.1f, 1f)
    return (lo - pad)..(hi + pad)
}

/**
 * Y range of TrendChart (hr `TrendChart.kt:29-45`): finite values plus [band] and [reference],
 * padded by 10 % of the span; flat data by max(1, |v|)·10 %; nothing finite: 0..1.
 */
internal fun trendRange(
    values: List<Float>,
    band: ClosedFloatingPointRange<Float>? = null,
    reference: Float? = null,
): ClosedFloatingPointRange<Float> {
    var lo = Float.POSITIVE_INFINITY
    var hi = Float.NEGATIVE_INFINITY
    fun take(v: Float) {
        if (v.isFinite()) {
            if (v < lo) lo = v
            if (v > hi) hi = v
        }
    }
    values.forEach { take(it) }
    if (band != null) { take(band.start); take(band.endInclusive) }
    if (reference != null) take(reference)
    if (lo > hi) return 0f..1f
    val span = hi - lo
    val pad = if (span > 0f) span * 0.1f else max(1f, abs(lo)) * 0.1f
    return (lo - pad)..(hi + pad)
}

/**
 * Indices whose x lies in [from]..[to] plus one neighbour per side (binary search; x ascending).
 * A window between two samples yields both; a window beyond the data yields the edge sample.
 */
internal fun visibleIndices(size: Int, from: Float, to: Float, xAt: (Int) -> Float): IntRange {
    if (size <= 0 || from.isNaN() || to.isNaN() || to < from) return IntRange.EMPTY
    var lo = 0
    var hi = size
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (xAt(mid) < from) lo = mid + 1 else hi = mid
    }
    val first = lo
    hi = size
    while (lo < hi) {
        val mid = (lo + hi) ushr 1
        if (xAt(mid) <= to) lo = mid + 1 else hi = mid
    }
    val lastIncl = lo - 1
    val a = (first - 1).coerceIn(0, size - 1)
    val b = (lastIncl + 1).coerceIn(0, size - 1)
    return if (a <= b) a..b else IntRange.EMPTY
}

/**
 * [range] reduced to about [maxPoints] points (hr `LiveScreen.kt:589-616`): means over chunks
 * anchored at absolute multiples of the chunk size (stable while a live window scrolls), the
 * last sample of [range] always raw. A chunk with a non-finite y becomes a NaN point (keeps gaps).
 * At most maxPoints + 2 points.
 */
internal fun decimate(
    range: IntRange,
    maxPoints: Int,
    xAt: (Int) -> Float,
    yAt: (Int) -> Float,
): List<ChartPoint> {
    if (range.isEmpty()) return emptyList()
    val n = range.last - range.first + 1
    val limit = maxPoints.coerceAtLeast(2)
    if (n <= limit) return range.map { ChartPoint(xAt(it), yAt(it)) }
    val chunk = (n + limit - 1) / limit
    val body = range.last - 1
    val out = ArrayList<ChartPoint>(limit + 2)
    var c = (range.first / chunk) * chunk
    while (c <= body) {
        val a = maxOf(c, range.first)
        val b = minOf(c + chunk - 1, body)
        var sx = 0.0
        var sy = 0.0
        var gap = false
        for (i in a..b) {
            val y = yAt(i)
            if (!y.isFinite()) gap = true else sy += y
            sx += xAt(i)
        }
        val cnt = b - a + 1
        out += ChartPoint((sx / cnt).toFloat(), if (gap) Float.NaN else (sy / cnt).toFloat())
        c += chunk
    }
    out += ChartPoint(xAt(range.last), yAt(range.last))
    return out
}

/** Index of the last finite y, or null. */
internal fun lastFiniteIndex(size: Int, yAt: (Int) -> Float): Int? {
    for (i in size - 1 downTo 0) if (yAt(i).isFinite()) return i
    return null
}
