package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeTest {
    private val s = Brand.Default.toColorScheme()
    private val red = Color(0xFFE8202E)

    @Test fun specSlots() {
        assertEquals(red, s.primary)
        assertEquals(Color(0xFFFFFFFF), s.onPrimary)
        assertEquals(lerp(Color(0xFF0A0A0A), red, 0.09f), s.primaryContainer)
        assertEquals(Color(0xFFA3A3AB), s.secondary)
        assertEquals(red, s.tertiary)
        assertEquals(Color(0xFFFF5A4F), s.error)
        assertEquals(Color(0xFF000000), s.background)
        assertEquals(Color(0xFF0B0B0D), s.surface)
        assertEquals(Color(0xFF1F1F23), s.surfaceVariant)
        assertEquals(Color(0xFF1F1F23), s.outline)
        assertEquals(Color(0xFF2A2A2F), s.outlineVariant)
        assertEquals(Color(0xFFF2F2F4), s.onBackground)
        assertEquals(Color(0xFFF2F2F4), s.onSurface)
        assertEquals(Color(0xFF8D8D94), s.onSurfaceVariant)
    }

    // Slots M3 components read (dialogs, cards, chips, snackbars) must not fall back to the purple baseline.
    @Test fun noBaselineSlotsLeak() {
        assertEquals(Color.Transparent, s.surfaceTint)
        assertEquals(Color(0xFF2A2A2F), s.secondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onSecondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onPrimaryContainer)
        assertEquals(Color(0xFF000000), s.surfaceContainerLowest)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainerLow)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainer)
        assertEquals(Color(0xFF141417), s.surfaceContainerHigh)
        assertEquals(Color(0xFF1F1F23), s.surfaceContainerHighest)
        assertEquals(Color(0xFFF2F2F4), s.inverseSurface)
        assertEquals(Color(0xFF0B0B0D), s.inverseOnSurface)
    }

    @Test fun customAccentRecolorsAccentSlotsOnly() {
        val blue = Color(0xFF2E6BE8)
        val b = Brand(accent = blue).toColorScheme()
        assertEquals(blue, b.primary)
        assertEquals(blue, b.tertiary)
        assertEquals(lerp(Color(0xFF0A0A0A), blue, 0.09f), b.primaryContainer)
        assertEquals(s.background, b.background)
        assertEquals(s.error, b.error)
    }

    @Test @Suppress("DEPRECATION") fun deprecatedValsKeepOldValues() {
        assertEquals(Color(0xFFB6A6F2), PrimaryPurple)
        assertEquals(Color(0xFF4CAF82), ConnectedGreen)
        assertEquals(5, ZoneColors.size)
    }
}
