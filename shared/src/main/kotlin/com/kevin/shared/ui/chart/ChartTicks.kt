package com.kevin.shared.ui.chart

import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

/** "Nice" time-axis steps in seconds. */
private val NICE_STEPS_SEC = intArrayOf(5, 10, 15, 30, 60, 120, 300, 600, 900, 1800, 3600, 7200)

/**
 * Tick positions (seconds, multiples of the step) within [fromSec]..[toSec] inclusive, using the
 * smallest nice step that yields at most [maxTicks] ticks. Port of hr `ui/shared/ChartTicks.kt:7-38`.
 */
internal fun timeTicks(fromSec: Float, toSec: Float, maxTicks: Int = 5): List<Float> {
    if (fromSec.isNaN() || toSec.isNaN() || toSec <= fromSec) return emptyList()
    val limit = maxTicks.coerceAtLeast(1)
    val steps = NICE_STEPS_SEC
    var chosen = steps.last()
    for (s in steps) {
        if (countTicks(fromSec, toSec, s) <= limit) { chosen = s; break }
    }
    return ticksFor(fromSec, toSec, chosen).let { if (it.size > limit) it.take(limit) else it }
}

private fun firstMultiple(fromSec: Float, step: Int): Long = ceil(fromSec / step).toLong()
private fun lastMultiple(toSec: Float, step: Int): Long = floor(toSec / step).toLong()

private fun countTicks(fromSec: Float, toSec: Float, step: Int): Int {
    val n = lastMultiple(toSec, step) - firstMultiple(fromSec, step) + 1
    return n.coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
}

private fun ticksFor(fromSec: Float, toSec: Float, step: Int): List<Float> {
    val a = firstMultiple(fromSec, step)
    val b = lastMultiple(toSec, step)
    if (b < a) return emptyList()
    return (a..b).map { (it * step).toFloat() }
}

/** "m:ss" below one hour (e.g. "0:30", "12:05"), "h:mm:ss" from one hour. hr `ChartTicks.kt:41-47`. */
internal fun formatTickLabel(sec: Float): String {
    val total = if (sec.isNaN() || sec < 0f) 0L else Math.round(sec).toLong()
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s)
    else String.format(Locale.ROOT, "%d:%02d", m, s)
}

/**
 * Indices of at most [maxLabels] category labels, evenly spaced and anchored at the last
 * (current) item: count 8, max 4 -> [1, 3, 5, 7]. Empty for count <= 0.
 */
internal fun labelIndices(count: Int, maxLabels: Int): List<Int> {
    if (count <= 0) return emptyList()
    val limit = maxLabels.coerceAtLeast(1)
    val step = (count - 1) / limit + 1 // ceil(count / limit) without overflow for limit = Int.MAX_VALUE
    return (count - 1 downTo 0 step step).toList().asReversed()
}
