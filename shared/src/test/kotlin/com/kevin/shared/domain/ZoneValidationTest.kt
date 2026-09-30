package com.kevin.shared.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ZoneValidationTest {

    @Test
    fun `valid sequential zones produce no errors`() {
        val zones = listOf("100" to "120", "120" to "140", "140" to "160", "160" to "180", "180" to "200")
        assertEquals(listOf(null, null, null, null, null), validateZoneTexts(zones))
    }

    @Test
    fun `inverted zone reports error`() {
        val zones = listOf("120" to "100")
        assertEquals(listOf(ZoneTextError.MinNotBelowMax), validateZoneTexts(zones))
    }

    @Test
    fun `overlapping zone reports error`() {
        val zones = listOf("100" to "120", "110" to "140")
        assertEquals(listOf(null, ZoneTextError.OverlapsPrevious(1)), validateZoneTexts(zones))
    }

    @Test
    fun `non numeric input reports error`() {
        val zones = listOf("abc" to "140")
        assertEquals(listOf(ZoneTextError.InvalidNumber), validateZoneTexts(zones))
    }
}
