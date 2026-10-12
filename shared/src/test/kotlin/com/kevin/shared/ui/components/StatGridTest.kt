package com.kevin.shared.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.em
import com.kevin.shared.format.NO_VALUE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class StatGridTest {
    @Test fun fullThreeByThree() {
        assertEquals(CellBorders(end = true, bottom = true), gridCellBorders(0, 9, 3))
        assertEquals(CellBorders(end = false, bottom = true), gridCellBorders(2, 9, 3))
        assertEquals(CellBorders(end = true, bottom = false), gridCellBorders(6, 9, 3))
        assertEquals(CellBorders(end = false, bottom = false), gridCellBorders(8, 9, 3))
    }

    @Test fun partialLastRowHasNoBottomAndNoTrailingEnd() {
        assertEquals(CellBorders(end = true, bottom = true), gridCellBorders(3, 7, 3))
        assertEquals(CellBorders(end = false, bottom = true), gridCellBorders(5, 7, 3))
        assertEquals(CellBorders(end = false, bottom = false), gridCellBorders(6, 7, 3))
        assertEquals(CellBorders(end = false, bottom = false), gridCellBorders(0, 1, 3))
    }

    @Test fun invalidGridIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { gridCellBorders(0, 9, 0) }
        assertThrows(IllegalArgumentException::class.java) { gridCellBorders(9, 9, 3) }
    }

    @Test fun unitIsASmallMutedSuffix() {
        val text = metricValueText("46", "ms", Color.Gray)
        assertEquals("46 ms", text.text)
        assertEquals(1, text.spanStyles.size)
        val span = text.spanStyles.single()
        assertEquals(2, span.start)
        assertEquals(Color.Gray, span.item.color)
        assertEquals(0.55.em, span.item.fontSize)
    }

    @Test fun noUnitOnMissingValueOrNullUnit() {
        assertEquals(NO_VALUE, metricValueText(NO_VALUE, "ms", Color.Gray).text)
        assertEquals("46", metricValueText("46", null, Color.Gray).text)
        assertTrue(metricValueText("46", "", Color.Gray).spanStyles.isEmpty())
    }
}
