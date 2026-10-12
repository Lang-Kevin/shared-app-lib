package com.kevin.shared.format

/** How [formatElapsed] renders a duration. No default on purpose: every call site picks one. */
enum class DurationStyle {
    /** Always `HH:MM:SS`, zero-padded (live timer, notification). */
    Hms,
    /** `MM:SS` under one hour, else `HH:MM:SS` (session lists, chart time). */
    Compact,
    /** `m:ss` under one hour, else `h:mm:ss`; the leading unit is unpadded (axis ticks, short clocks). */
    Clock,
}

/** Formats [seconds] of elapsed time. Negative input counts as 0. ASCII digits in every locale. */
fun formatElapsed(seconds: Long, style: DurationStyle): String {
    val total = seconds.coerceAtLeast(0)
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return when (style) {
        DurationStyle.Hms -> "${pad2(h)}:${pad2(m)}:${pad2(s)}"
        DurationStyle.Compact -> if (h > 0) "${pad2(h)}:${pad2(m)}:${pad2(s)}" else "${pad2(m)}:${pad2(s)}"
        DurationStyle.Clock -> if (h > 0) "$h:${pad2(m)}:${pad2(s)}" else "$m:${pad2(s)}"
    }
}

private fun pad2(value: Long): String = value.toString().padStart(2, '0')
