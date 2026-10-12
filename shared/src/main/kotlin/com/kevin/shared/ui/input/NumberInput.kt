package com.kevin.shared.ui.input

import java.math.BigDecimal
import java.math.RoundingMode

/** Result of parsing a numeric text field. */
sealed interface NumberInput {
    data object Empty : NumberInput
    data object Invalid : NumberInput
    data class OutOfRange(val value: Double) : NumberInput
    data class Valid(val value: Double) : NumberInput
}

private val IntegerPattern = Regex("[+-]?\\d+")
private val DecimalPattern = Regex("[+-]?(\\d+(\\.\\d*)?|\\.\\d+)")

/**
 * Parses user input: blank -> Empty, ',' accepted as decimal separator, [decimals] == 0 accepts integers only.
 * [range] is checked on the raw value before rounding HALF_UP to [decimals].
 */
fun parseNumberInput(text: String, range: ClosedFloatingPointRange<Double>? = null, decimals: Int): NumberInput {
    require(decimals >= 0) { "decimals must be >= 0" }
    val normalized = text.trim().replace(',', '.')
    if (normalized.isEmpty()) return NumberInput.Empty
    val pattern = if (decimals == 0) IntegerPattern else DecimalPattern
    if (!pattern.matches(normalized)) return NumberInput.Invalid
    val raw = normalized.toDoubleOrNull()?.takeIf { it.isFinite() } ?: return NumberInput.Invalid
    if (range != null && raw !in range) return NumberInput.OutOfRange(raw)
    return NumberInput.Valid(BigDecimal.valueOf(raw).setScale(decimals, RoundingMode.HALF_UP).toDouble())
}

/** True when the field should show its error: Invalid or OutOfRange. Empty is not an error. */
val NumberInput.isError: Boolean
    get() = this is NumberInput.Invalid || this is NumberInput.OutOfRange

fun NumberInput.valueOrNull(): Double? = (this as? NumberInput.Valid)?.value
