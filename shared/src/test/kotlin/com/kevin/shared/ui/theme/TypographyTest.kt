package com.kevin.shared.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Test

class TypographyTest {
    private val t = AppTypography

    @Test fun heroNumbersAreLight() {
        assertEquals(64.sp, t.displayLarge.fontSize)
        assertEquals(FontWeight.Light, t.displayLarge.fontWeight)
        assertEquals(52.sp, t.displayMedium.fontSize)
        assertEquals(FontWeight.Light, t.displayMedium.fontWeight)
    }

    @Test fun labelsAndTitlesAreTracked() {
        assertEquals(FontWeight.Normal, t.labelSmall.fontWeight)
        assertEquals(9.5.sp, t.labelSmall.fontSize)
        assertEquals(0.16.em, t.labelSmall.letterSpacing)
        assertEquals(FontWeight.Medium, t.titleMedium.fontWeight)
        assertEquals(0.2.em, t.titleMedium.letterSpacing)
        assertEquals(FontWeight.Normal, t.headlineSmall.fontWeight)
    }

    @Test fun everyStyleUsesSpaceGrotesk() {
        listOf(t.displayLarge, t.bodyMedium, t.labelSmall, t.titleMedium)
            .forEach { assertEquals(SpaceGroteskFamily, it.fontFamily) }
    }
}
