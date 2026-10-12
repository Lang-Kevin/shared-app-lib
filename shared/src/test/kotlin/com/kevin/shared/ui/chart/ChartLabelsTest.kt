package com.kevin.shared.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Test

class ChartLabelsTest {

    @Test
    fun `eight weeks four labels end at current week`() {
        assertEquals(listOf(1, 3, 5, 7), labelIndices(8, 4))
    }

    @Test
    fun `odd count keeps first and last`() {
        assertEquals(listOf(0, 2, 4), labelIndices(5, 4))
    }

    @Test
    fun `count within limit shows all`() {
        assertEquals(listOf(0, 1, 2, 3), labelIndices(4, 4))
        assertEquals(listOf(0, 1, 2), labelIndices(3, 10))
        assertEquals(listOf(0, 1, 2), labelIndices(3, Int.MAX_VALUE))
    }

    @Test
    fun `single item`() {
        assertEquals(listOf(0), labelIndices(1, 4))
    }

    @Test
    fun `empty count`() {
        assertEquals(emptyList<Int>(), labelIndices(0, 4))
        assertEquals(emptyList<Int>(), labelIndices(-3, 4))
    }

    @Test
    fun `max labels below one shows only the last`() {
        assertEquals(listOf(7), labelIndices(8, 0))
    }

    @Test
    fun `tick label uses ascii digits regardless of default locale`() {
        val saved = java.util.Locale.getDefault()
        try {
            java.util.Locale.setDefault(java.util.Locale.forLanguageTag("ar-EG"))
            assertEquals("1:02:03", formatTickLabel(3723f))
        } finally {
            java.util.Locale.setDefault(saved)
        }
    }
}
