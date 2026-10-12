package com.kevin.shared.format

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale

/** Fixed numeric layouts shared by both apps; only the weekday abbreviation follows the locale. */
enum class DateStyle(internal val pattern: String, internal val hasTime: Boolean) {
    DayMonth("dd.MM", false),              // "12.10" (week labels)
    ShortDate("dd.MM.yy", false),          // "12.10.26"
    Date("dd.MM.yyyy", false),             // "12.10.2026"
    WeekdayDate("EEE, dd.MM.yyyy", false), // "Mon, 12.10.2026"
    WeekdayTime("EEE HH:mm", true),        // "Mon 18:42" (de) / "Mon 6:42 PM" (en-US); pattern overridden per locale
    Time("HH:mm", true),                   // "18:42"
    DateTime("dd.MM.yyyy HH:mm", true),    // "12.10.2026 18:42"
}

fun formatDate(epochMillis: Long, style: DateStyle, locale: Locale, zone: ZoneId): String =
    DateTimeFormatter.ofPattern(patternFor(style, locale), locale).format(Instant.ofEpochMilli(epochMillis).atZone(zone))

/** WeekdayTime takes the locale's SHORT time pattern (12h AM/PM for en-US, 24h for de). */
private fun patternFor(style: DateStyle, locale: Locale): String =
    if (style == DateStyle.WeekdayTime) {
        "EEE " + DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, IsoChronology.INSTANCE, locale)
    } else style.pattern

/** Calendar-day overload; [style] must not contain a time. */
fun formatDate(date: LocalDate, style: DateStyle, locale: Locale): String {
    require(!style.hasTime) { "$style needs a time; use the epochMillis overload" }
    return DateTimeFormatter.ofPattern(style.pattern, locale).format(date)
}

/** "start – end" (en dash), e.g. for a date-range filter chip. */
fun formatDateRange(start: LocalDate, end: LocalDate, style: DateStyle, locale: Locale): String =
    "${formatDate(start, style, locale)} – ${formatDate(end, style, locale)}"

/** M3 DatePicker/DateRangePicker millis are UTC midnight of the picked calendar day. */
fun pickerMillisToLocalDate(utcMillis: Long): LocalDate =
    Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate()

/** Inverse of [pickerMillisToLocalDate], for pre-selecting a day in the picker. */
fun LocalDate.toPickerMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

/** Local midnight of this day in [zone], as epoch millis (start of a filter window). */
fun LocalDate.startOfDayMillis(zone: ZoneId): Long = atStartOfDay(zone).toInstant().toEpochMilli()
