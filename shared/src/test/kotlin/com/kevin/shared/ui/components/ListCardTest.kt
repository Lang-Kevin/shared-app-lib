package com.kevin.shared.ui.components

import androidx.compose.ui.graphics.Color
import com.kevin.shared.format.NO_VALUE
import org.junit.Assert.assertEquals
import org.junit.Test

class ListCardTest {
    private fun style(selected: Boolean, highlighted: Boolean) = listCardStyle(
        selected, highlighted, surface = Color.Black, outline = Color.Gray, accent = Color.Red, accentTint = Color.DarkGray,
    )

    @Test fun plainCardIsHairline() {
        assertEquals(ListCardStyle(Color.Black, Color.Gray), style(selected = false, highlighted = false))
    }

    @Test fun selectedIsAccentBorderPlusTint() {
        assertEquals(ListCardStyle(Color.DarkGray, Color.Red), style(selected = true, highlighted = false))
        assertEquals(ListCardStyle(Color.DarkGray, Color.Red), style(selected = true, highlighted = true))
    }

    @Test fun highlightedIsAccentBorderOnly() {
        assertEquals(ListCardStyle(Color.Black, Color.Red), style(selected = false, highlighted = true))
    }

    @Test fun missingTrailingValueIsADashWithoutUnit() {
        assertEquals(NO_VALUE, metricValueText(NO_VALUE, "cm", Color.Gray).text)
    }
}
