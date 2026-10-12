package com.kevin.shared.format

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale

class DatesTest {
    private val berlin = ZoneId.of("Europe/Berlin")
    private val utc = ZoneOffset.UTC
    private val monday = Instant.parse("2026-10-12T16:42:00Z").toEpochMilli()

    @Test fun fixedPatternsInZone() {
        assertEquals("12.10", formatDate(monday, DateStyle.DayMonth, Locale.US, utc))
        assertEquals("12.10.26", formatDate(monday, DateStyle.ShortDate, Locale.US, utc))
        assertEquals("12.10.2026", formatDate(monday, DateStyle.Date, Locale.US, utc))
        assertEquals("16:42", formatDate(monday, DateStyle.Time, Locale.US, utc))
        assertEquals("18:42", formatDate(monday, DateStyle.Time, Locale.US, berlin))
        assertEquals("12.10.2026 18:42", formatDate(monday, DateStyle.DateTime, Locale.US, berlin))
    }

    @Test fun weekdayFollowsLocale() {
        assertEquals("Mon, 12.10.2026", formatDate(monday, DateStyle.WeekdayDate, Locale.US, utc))
        assertEquals("Mon 16:42", formatDate(monday, DateStyle.WeekdayTime, Locale.US, utc))
    }

    @Test fun zoneMovesTheDay() {
        val lateUtc = Instant.parse("2026-10-12T23:30:00Z").toEpochMilli()
        assertEquals("12.10.2026", formatDate(lateUtc, DateStyle.Date, Locale.US, utc))
        assertEquals("13.10.2026", formatDate(lateUtc, DateStyle.Date, Locale.US, berlin))
    }

    @Test fun localDateOverloadAndRange() {
        val start = LocalDate.of(2026, 10, 5)
        val end = LocalDate.of(2026, 10, 12)
        assertEquals("05.10.26", formatDate(start, DateStyle.ShortDate, Locale.US))
        assertEquals("05.10.26 – 12.10.26", formatDateRange(start, end, DateStyle.ShortDate, Locale.US))
        assertThrows(IllegalArgumentException::class.java) { formatDate(start, DateStyle.Time, Locale.US) }
    }

    @Test fun pickerMillisRoundTrip() {
        val day = LocalDate.of(2026, 10, 12)
        assertEquals(Instant.parse("2026-10-12T00:00:00Z").toEpochMilli(), day.toPickerMillis())
        assertEquals(day, pickerMillisToLocalDate(day.toPickerMillis()))
    }

    @Test fun startOfDayMatchesHrViewModelFormula() {
        val picked = LocalDate.of(2026, 10, 12).toPickerMillis()
        for (zone in listOf(berlin, ZoneId.of("America/Los_Angeles"), utc)) {
            // hr HistoryViewModel.setDateRange, verbatim:
            val hr = Instant.ofEpochMilli(picked).atZone(ZoneOffset.UTC).toLocalDate()
                .atStartOfDay(zone).toInstant().toEpochMilli()
            assertEquals(hr, pickerMillisToLocalDate(picked).startOfDayMillis(zone))
        }
        assertEquals(
            Instant.parse("2026-10-11T22:00:00Z").toEpochMilli(),
            LocalDate.of(2026, 10, 12).startOfDayMillis(berlin),
        )
    }
}
