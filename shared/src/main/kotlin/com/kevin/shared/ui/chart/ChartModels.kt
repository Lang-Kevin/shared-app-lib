package com.kevin.shared.ui.chart

/**
 * One line series. x is the sample index unless [xs] is given (finite, ascending, same size).
 * A non-finite value in [ys] is a gap.
 */
data class LineSeries(val ys: List<Float>, val xs: List<Float>? = null) {
    init {
        require(xs == null || xs.size == ys.size) { "xs (${xs?.size}) and ys (${ys.size}) differ in size" }
    }

    val size: Int get() = ys.size

    fun xAt(i: Int): Float = xs?.get(i) ?: i.toFloat()

    fun yAt(i: Int): Float = ys[i]
}

/**
 * Zone band [lo]..[hi]. [number] is 1-based (1 = Z1) and picks `AppTheme.colors.zones[number - 1]`
 * (clamped). [label] is drawn above the lower bound line, e.g. "Z1 93" (caller-built).
 */
data class ChartZone(val number: Int, val lo: Float, val hi: Float, val label: String)

/** Vertical event marker (e.g. milestone "M1") at data x [x]. */
data class ChartMarker(val x: Float, val label: String)

/**
 * Dashed accent reference line at y = [value] (e.g. session mean "Ø 141"). [segments] (data x
 * ranges) restricts it, e.g. to connection gaps (hr `TrainingUi.kt:237-261`); null = full width.
 */
data class ChartReferenceLine(
    val value: Float,
    val label: String? = null,
    val segments: List<ClosedFloatingPointRange<Float>>? = null,
)

/** A point in data units. */
internal data class ChartPoint(val x: Float, val y: Float)
