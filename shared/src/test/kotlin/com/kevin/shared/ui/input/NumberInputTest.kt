package com.kevin.shared.ui.input

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberInputTest {
    @Test fun blankIsEmptyNotError() {
        assertEquals(NumberInput.Empty, parseNumberInput("", decimals = 2))
        assertEquals(NumberInput.Empty, parseNumberInput("   ", decimals = 0))
        assertFalse(NumberInput.Empty.isError)
    }

    @Test fun garbageIsInvalid() {
        assertEquals(NumberInput.Invalid, parseNumberInput("abc", decimals = 2))
        assertEquals(NumberInput.Invalid, parseNumberInput("NaN", decimals = 2))
        assertEquals(NumberInput.Invalid, parseNumberInput("1e3", decimals = 2))
        assertEquals(NumberInput.Invalid, parseNumberInput("1.2.3", decimals = 2))
        assertTrue(NumberInput.Invalid.isError)
    }

    @Test fun commaIsADecimalSeparator() {
        assertEquals(NumberInput.Valid(12.5), parseNumberInput("12,5", decimals = 2))
        assertEquals(NumberInput.Valid(0.5), parseNumberInput(",5", decimals = 1))
    }

    @Test fun roundsHalfUpAfterTheRangeCheck() {
        assertEquals(NumberInput.Valid(12.35), parseNumberInput("12.345", decimals = 2))
        // raw 300.004 is outside 0.5..300 even though it would round to 300.00
        assertEquals(NumberInput.OutOfRange(300.004), parseNumberInput("300.004", 0.5..300.0, 2))
    }

    @Test fun integerFieldsRejectDecimals() {
        assertEquals(NumberInput.Invalid, parseNumberInput("12.5", decimals = 0))
        assertEquals(NumberInput.Valid(40.0), parseNumberInput(" 40 ", 10.0..150.0, 0))
    }

    @Test fun jumpLoadParity() {
        val load = 0.5..300.0
        assertEquals(0.5, parseNumberInput("0.5", load, 2).valueOrNull())
        assertEquals(300.0, parseNumberInput("300", load, 2).valueOrNull())
        assertEquals(NumberInput.OutOfRange(0.4), parseNumberInput("0,4", load, 2))
        assertNull(parseNumberInput("0,4", load, 2).valueOrNull())
        assertTrue(parseNumberInput("0,4", load, 2).isError)
    }

    @Test fun jumpBoxHeightParity() {
        val box = 10.0..150.0
        assertEquals(NumberInput.OutOfRange(9.0), parseNumberInput("9", box, 0))
        assertEquals(NumberInput.OutOfRange(151.0), parseNumberInput("151", box, 0))
        assertEquals(150.0, parseNumberInput("150", box, 0).valueOrNull())
    }

    @Test fun negativeDecimalsAreAProgrammingError() {
        assertThrows(IllegalArgumentException::class.java) { parseNumberInput("1", decimals = -1) }
    }
}
