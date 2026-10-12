package com.kevin.shared.ui.dialog

import com.kevin.shared.format.toPickerMillis
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProgressAndDateRangeTest {
    @Test fun fractionOfCounters() {
        assertEquals(0.25f, progressFraction(1, 4), 0.0001f)
        assertEquals(1f, progressFraction(5, 4), 0.0001f)
        assertEquals(0f, progressFraction(-1, 4), 0.0001f)
        assertEquals(0f, progressFraction(3, 0), 0.0001f)
    }

    @Test fun progressIsClamped() {
        assertEquals(0f, clampProgress(Float.NaN), 0f)
        assertEquals(1f, clampProgress(1.7f), 0f)
        assertEquals(0f, clampProgress(-0.2f), 0f)
        assertEquals(0.5f, clampProgress(0.5f), 0f)
    }

    @Test fun rangeNeedsBothEnds() {
        val start = LocalDate.of(2026, 3, 29)
        assertNull(dateRangeSelection(null, null))
        assertNull(dateRangeSelection(start.toPickerMillis(), null))
        assertNull(dateRangeSelection(null, start.toPickerMillis()))
    }

    @Test fun rangeRoundTripsLocalDatesAcrossDst() {
        // 2026-03-29 is the EU DST switch; picker millis are UTC midnight, so no shift may occur.
        val start = LocalDate.of(2026, 3, 29)
        val end = LocalDate.of(2026, 4, 2)
        assertEquals(start to end, dateRangeSelection(start.toPickerMillis(), end.toPickerMillis()))
    }
}
