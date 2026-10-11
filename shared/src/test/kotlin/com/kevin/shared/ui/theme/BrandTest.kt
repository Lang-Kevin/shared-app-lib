package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BrandTest {
    private val blue = Color(0xFF2E6BE8)

    @Test fun defaultAccentIsNightDriveRed() {
        assertEquals(Color(0xFFE8202E), Brand.Default.accent)
        assertEquals(Color(0xFFFFFFFF), Brand.Default.onAccent)
    }

    @Test fun zonesFollowTheDocumentedRamp() {
        val zones = Brand.Default.toAppColors().zones
        assertEquals(5, zones.size)
        val graphite = Color(0xFF26262B)
        listOf(0.10f, 0.28f, 0.50f, 0.75f).forEachIndexed { i, f ->
            assertEquals(lerp(graphite, Brand.Default.accent, f), zones[i])
        }
        assertEquals(Brand.Default.accent, zones[4])
    }

    @Test fun recordingAndZ5AreExactlyTheAccent() {
        val colors = Brand(accent = blue).toAppColors()
        assertEquals(blue, colors.recording)
        assertEquals(blue, colors.zones[4])
    }

    @Test fun customAccentRederivesTheRamp() {
        val red = Brand.Default.toAppColors()
        val custom = Brand(accent = blue).toAppColors()
        assertEquals(lerp(Color(0xFF26262B), blue, 0.50f), custom.zones[2])
        assertNotEquals(red.zones[2], custom.zones[2])
        assertEquals(lerp(Color(0xFF0A0A0A), blue, 0.09f), custom.accentTint)
        assertEquals(blue.copy(alpha = 0.40f), custom.accentSoft)
    }

    @Test fun fixedRolesMatchSpec() {
        val c = Brand.Default.toAppColors()
        assertEquals(Color(0xFF55555C), c.textDisabled)
        assertEquals(Color(0xFF3FB27F), c.success)
        assertEquals(Color(0xFFFFB74D), c.warning)
        assertEquals(Color(0xFFFFC850), c.highlight)
        assertEquals(Color.White, c.onMedia)
        assertEquals(Color.Black.copy(alpha = 0.28f), c.mediaScrim)
        assertEquals(Color.Black.copy(alpha = 0.78f), c.scrimStrong)
        assertEquals(Color.White.copy(alpha = 0.07f), c.gridLine)
        assertEquals(Color(0xE60B0B0D), c.tooltipContainer)
        assertEquals(Color.Black.copy(alpha = 0.75f), c.mediaTextShadow.color)
        assertEquals(3f, c.mediaTextShadow.blurRadius)
    }
}
