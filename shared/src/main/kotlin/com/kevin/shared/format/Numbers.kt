package com.kevin.shared.format

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** Shown instead of a number that doesn't exist (NaN, infinite, not measured). */
const val NO_VALUE: String = "—"

/**
 * Rounds HALF_UP to [maxDecimals] on the shortest decimal form of [value] (2.675 -> "2.68", like "%.2f"),
 * keeps at least [minDecimals], never groups thousands and takes the decimal separator from [locale].
 * NaN/infinite -> [NO_VALUE]. A result that rounds to zero has no minus sign.
 */
fun formatDecimal(value: Double, maxDecimals: Int, locale: Locale, minDecimals: Int = 0): String {
    require(maxDecimals >= 0) { "maxDecimals must be >= 0" }
    require(minDecimals in 0..maxDecimals) { "minDecimals must be in 0..maxDecimals" }
    if (!value.isFinite()) return NO_VALUE
    val rounded = BigDecimal.valueOf(value).setScale(maxDecimals, RoundingMode.HALF_UP)
    val format = DecimalFormat("0", DecimalFormatSymbols.getInstance(locale)).apply {
        minimumFractionDigits = minDecimals
        maximumFractionDigits = maxDecimals
        isGroupingUsed = false
    }
    return format.format(if (rounded.signum() == 0) BigDecimal.ZERO else rounded)
}

/** "[number] [unit]" joined by a no-break space (never wraps), e.g. "42.5 cm". Non-finite -> [NO_VALUE] alone. */
fun formatMeasure(value: Double, unit: String, maxDecimals: Int, locale: Locale, minDecimals: Int = 0): String {
    val number = formatDecimal(value, maxDecimals, locale, minDecimals)
    return if (number == NO_VALUE) NO_VALUE else "$number $unit"
}

/** [fraction] in 0..1 (clamped, NaN -> 0) as a whole percent, e.g. 0.25 -> "25%". */
fun formatPercent(fraction: Float, locale: Locale): String {
    val clamped = if (fraction.isNaN()) 0f else fraction.coerceIn(0f, 1f)
    return formatDecimal(clamped * 100.0, 0, locale) + "%"
}
