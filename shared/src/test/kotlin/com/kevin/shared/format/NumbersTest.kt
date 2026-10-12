package com.kevin.shared.format

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.Locale

class NumbersTest {
    @Test fun roundsHalfUpLikeStringFormat() {
        assertEquals("1.3", formatDecimal(1.25, 1, Locale.US))      // HALF_EVEN would give 1.2
        assertEquals("2.68", formatDecimal(2.675, 2, Locale.US))    // shortest decimal form, like "%.2f"
        assertEquals("-1.3", formatDecimal(-1.25, 1, Locale.US))
    }

    @Test fun trimsTrailingZerosUnlessMinDecimals() {
        assertEquals("1.5", formatDecimal(1.5, 2, Locale.US))
        assertEquals("1.50", formatDecimal(1.5, 2, Locale.US, minDecimals = 2))
        assertEquals("80", formatDecimal(80.0, 2, Locale.US))
    }

    @Test fun noGroupingAndLocaleSeparator() {
        assertEquals("12346", formatDecimal(12345.6, 0, Locale.US))
        assertEquals("1,3", formatDecimal(1.25, 1, Locale.GERMANY))
    }

    @Test fun negativeZeroLosesItsSign() {
        assertEquals("0", formatDecimal(-0.04, 1, Locale.US))
        assertEquals("0.0", formatDecimal(-0.04, 1, Locale.US, minDecimals = 1))
    }

    @Test fun nonFiniteIsNoValue() {
        assertEquals(NO_VALUE, formatDecimal(Double.NaN, 1, Locale.US))
        assertEquals(NO_VALUE, formatDecimal(Double.POSITIVE_INFINITY, 1, Locale.US))
        assertEquals("—", NO_VALUE)
    }

    @Test fun invalidDecimalsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) { formatDecimal(1.0, -1, Locale.US) }
        assertThrows(IllegalArgumentException::class.java) { formatDecimal(1.0, 1, Locale.US, minDecimals = 2) }
    }

    @Test fun measureJoinsWithNoBreakSpace() {
        assertEquals("42 cm", formatMeasure(42.0, "cm", 1, Locale.US))
        assertEquals("80.25 kg", formatMeasure(80.25, "kg", 2, Locale.US))
        assertEquals("42,5 cm", formatMeasure(42.5, "cm", 1, Locale.GERMANY))
        assertEquals(NO_VALUE, formatMeasure(Double.NaN, "cm", 1, Locale.US))
    }

    @Test fun percentIsClampedWholeNumber() {
        assertEquals("25%", formatPercent(0.25f, Locale.US))
        assertEquals("100%", formatPercent(0.996f, Locale.US))
        assertEquals("100%", formatPercent(1.5f, Locale.US))
        assertEquals("0%", formatPercent(-1f, Locale.US))
        assertEquals("0%", formatPercent(Float.NaN, Locale.US))
    }
}
