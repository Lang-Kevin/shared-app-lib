package com.kevin.shared.format

import com.kevin.shared.ui.session.durationString
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationsTest {
    @Test fun hmsAlwaysShowsPaddedHours() {
        assertEquals("00:00:00", formatElapsed(0, DurationStyle.Hms))
        assertEquals("00:00:59", formatElapsed(59, DurationStyle.Hms))
        assertEquals("01:01:01", formatElapsed(3661, DurationStyle.Hms))
        assertEquals("100:00:00", formatElapsed(360_000, DurationStyle.Hms))
    }

    @Test fun compactDropsHoursUnderOneHour() {
        assertEquals("00:00", formatElapsed(0, DurationStyle.Compact))
        assertEquals("59:59", formatElapsed(3599, DurationStyle.Compact))
        assertEquals("01:00:00", formatElapsed(3600, DurationStyle.Compact))
    }

    @Test fun clockLeavesLeadingUnitUnpadded() {
        assertEquals("0:00", formatElapsed(0, DurationStyle.Clock))
        assertEquals("1:05", formatElapsed(65, DurationStyle.Clock))
        assertEquals("59:59", formatElapsed(3599, DurationStyle.Clock))
        assertEquals("1:00:00", formatElapsed(3600, DurationStyle.Clock))
        assertEquals("10:02:05", formatElapsed(36_125, DurationStyle.Clock))
    }

    @Test fun negativeCountsAsZero() {
        assertEquals("00:00:00", formatElapsed(-5, DurationStyle.Hms))
        assertEquals("0:00", formatElapsed(-1, DurationStyle.Clock))
    }

    @Test fun durationStringKeepsItsOutput() {
        assertEquals("00:00", durationString(0))
        assertEquals("59:59", durationString(3599))
        assertEquals("01:02:05", durationString(3725))
    }
}
