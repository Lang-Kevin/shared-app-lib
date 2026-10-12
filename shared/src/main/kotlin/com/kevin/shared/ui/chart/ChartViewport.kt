package com.kevin.shared.ui.chart

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Visible X range of a chart as fractions (0..1) of the full data width (Detail charts).
 * Pure Kotlin, JVM-testable. Port of hr `ui/shared/ChartViewport.kt:13-76` (fe31536).
 */
data class ChartViewport(val start: Float = 0f, val end: Float = 1f) {

    val span: Float get() = end - start

    /** true when the whole data range is visible. */
    val isFull: Boolean get() = start <= EPS && end >= 1f - EPS

    /**
     * Zooms by [factor] (>1 = in). [anchor] (0..1) is the position inside the visible width;
     * the data point under it stays there (except when clamped at 0/1).
     */
    fun zoomBy(factor: Float, anchor: Float, minSpan: Float): ChartViewport {
        if (factor <= 0f || factor.isNaN() || factor.isInfinite()) return this
        val a = anchor.coerceIn(0f, 1f)
        val newSpan = (span / factor).coerceIn(minSpan.coerceIn(TINY, 1f), 1f)
        val focus = start + a * span
        val newStart = (focus - a * newSpan).coerceIn(0f, 1f - newSpan)
        return snapped(newStart, newSpan)
    }

    /**
     * Pans by [deltaFraction] of the visible width. Positive = content moves right, i.e. earlier
     * data becomes visible (like swiping right). The span is kept.
     */
    fun panBy(deltaFraction: Float): ChartViewport {
        val s = span
        val newStart = (start - deltaFraction * s).coerceIn(0f, 1f - s)
        return snapped(newStart, s)
    }

    /** Snaps to the exact edges so float noise never yields "almost full". */
    private fun snapped(start: Float, span: Float): ChartViewport {
        var s = start
        var e = start + span
        val atEnd = e > 1f - EPS
        if (atEnd) { e = 1f; s = 1f - span }
        if (s < EPS) { s = 0f; if (!atEnd) e = span }
        return ChartViewport(s, e)
    }

    /** Position of [fraction] (data range) inside the visible window; may lie outside 0..1. */
    fun mapX(fraction: Float): Float = (fraction - start) / span

    /**
     * Indices of samples whose fraction i/(size-1) lies in [start, end], plus one neighbour on
     * each side so the line reaches the edge.
     */
    fun visibleIndexRange(size: Int): IntRange {
        if (size <= 0) return IntRange.EMPTY
        if (size == 1) return 0..0
        val n = (size - 1).toFloat()
        val first = ceil(start * n - 1e-4f).toInt() - 1
        val last = floor(end * n + 1e-4f).toInt() + 1
        val lo = first.coerceIn(0, size - 1)
        val hi = last.coerceIn(0, size - 1)
        return if (lo <= hi) lo..hi else IntRange.EMPTY
    }

    /** Visible window in data units for data spanning 0..[totalX] (e.g. session seconds). */
    fun toWindow(totalX: Float): ClosedFloatingPointRange<Float> {
        val t = if (totalX.isFinite() && totalX > 0f) totalX else 0f
        return (start * t)..(end * t)
    }

    companion object {
        val Full = ChartViewport()
        private const val EPS = 1e-4f
        private const val TINY = 1e-4f
    }
}

/**
 * Index of the sample under [fraction] (0..1 across the visible plot width), mapped linearly onto
 * range.first..range.last, rounded and clamped; null for an empty range or NaN.
 */
fun nearestIndex(fraction: Float, range: IntRange): Int? {
    if (range.isEmpty()) return null
    if (fraction.isNaN()) return null
    val f = fraction.coerceIn(0f, 1f)
    val idx = range.first + (f * (range.last - range.first)).roundToInt()
    return idx.coerceIn(range.first, range.last)
}

/**
 * Live window in samples (hr: 1 sample ≈ 1 s). [windowSeconds] is the (float) width, [anchorEnd]
 * the absolute index of the right edge when scrolled back; null = follows the newest sample.
 * Because the anchor is absolute, the view stays put while samples are appended, and many small
 * pan/zoom steps keep sub-sample precision. Port of hr `ui/shared/ChartViewport.kt:96-151`.
 */
data class LiveWindow(val windowSeconds: Float = DEFAULT.toFloat(), val anchorEnd: Float? = null) {

    val isFollowing: Boolean get() = anchorEnd == null

    /** Visible sample indices for [total] samples; empty when total <= 0. */
    fun visibleRange(total: Int): IntRange {
        if (total <= 0) return IntRange.EMPTY
        val w = windowSeconds.roundToInt().coerceIn(1, max(1, total))
        val end = if (anchorEnd == null) total - 1
        else anchorEnd.roundToInt().coerceIn(min(w - 1, total - 1), total - 1)
        val start = (end - w + 1).coerceAtLeast(0)
        return start..end
    }

    /**
     * Zooms by [factor] (>1 = in) around [anchor] (0..1 of the visible width); the sample under
     * the anchor stays fixed. When everything is visible only the stored width changes.
     * [minWindow]/[maxWindow] bound the width (defaults: hr's 30 s / 6 h).
     */
    fun zoomBy(
        factor: Float,
        anchor: Float,
        total: Int,
        minWindow: Float = MIN.toFloat(),
        maxWindow: Float = MAX_WINDOW,
    ): LiveWindow {
        if (factor <= 0f || factor.isNaN() || factor.isInfinite()) return this
        val a = anchor.coerceIn(0f, 1f)
        val effW = min(windowSeconds, total.toFloat()).coerceAtLeast(1f)
        val newW = (effW / factor).coerceAtLeast(minWindow)
        if (newW >= total) {
            return copy(windowSeconds = max(newW, windowSeconds).coerceAtMost(maxWindow))
        }
        val lastIdx = total - 1f
        val currentEnd = (anchorEnd ?: lastIdx).coerceIn(min(effW - 1f, lastIdx), lastIdx)
        val anchorSample = currentEnd - (1f - a) * (effW - 1f)
        val newEnd = anchorSample + (1f - a) * (newW - 1f)
        return when {
            isFollowing && a >= 0.999f -> LiveWindow(newW, null)
            newEnd >= lastIdx -> LiveWindow(newW, null)
            else -> LiveWindow(newW, newEnd.coerceAtLeast(min(newW - 1f, lastIdx)))
        }
    }

    /** Pans by [deltaSeconds]; positive = further into the past. */
    fun panBy(deltaSeconds: Float, total: Int): LiveWindow {
        if (total <= 0 || deltaSeconds.isNaN()) return this
        val effW = min(windowSeconds, total.toFloat()).coerceAtLeast(1f)
        val lastIdx = total - 1f
        val base = anchorEnd ?: lastIdx
        val newEnd = (base - deltaSeconds).coerceAtLeast(min(effW - 1f, lastIdx))
        // Snap to live only when scrolling back towards it; moving away from the live edge must
        // let many small steps (< 0.5 s) add up.
        return if (newEnd >= lastIdx || (deltaSeconds <= 0f && newEnd >= lastIdx - 0.5f)) copy(anchorEnd = null)
        else copy(anchorEnd = newEnd)
    }

    /** Visible window in index units (x = sample index); 0..0 when empty. */
    fun toWindow(total: Int): ClosedFloatingPointRange<Float> {
        val r = visibleRange(total)
        return if (r.isEmpty()) 0f..0f else r.first.toFloat()..r.last.toFloat()
    }

    companion object {
        const val DEFAULT = 120
        const val MIN = 30
        const val MAX_WINDOW = 21600f
    }
}
