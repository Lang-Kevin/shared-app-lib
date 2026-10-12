package com.kevin.shared.ui.chart

import androidx.compose.ui.graphics.Color
import com.kevin.shared.ui.theme.Brand
import com.kevin.shared.ui.theme.toAppColors
import com.kevin.shared.ui.theme.toColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ChartColorsTest {

    private val scheme = Brand.Default.toColorScheme()
    private val app = Brand.Default.toAppColors()
    private val c = chartColors(scheme, app)

    @Test
    fun `accent roles follow scheme primary`() {
        assertEquals(scheme.primary, c.accent)
        assertEquals(scheme.primary, c.goalBorder)
        assertEquals(scheme.primary, c.barHighlightBorder)
        assertEquals(scheme.primary.copy(alpha = 0.08f), c.targetBand)
        assertEquals(app.accentTint, c.goalFill)
        assertEquals(app.accentTint, c.barHighlight)
    }

    @Test
    fun `neutral roles come from tokens`() {
        assertEquals(scheme.onSurface, c.line)
        assertEquals(scheme.onSurface.copy(alpha = 0.45f), c.secondaryLine)
        assertEquals(scheme.onSurface.copy(alpha = 0.5f), c.scrubLine)
        assertEquals(scheme.onSurface.copy(alpha = 0.04f), c.neutralBand)
        assertEquals(app.textDisabled, c.axisLabel)
        assertEquals(scheme.onSurfaceVariant, c.zoneLabel)
        assertEquals(app.highlight, c.marker)
        assertEquals(app.highlight.copy(alpha = 0.7f), c.markerLine)
        assertEquals(app.tooltipContainer, c.tooltipFill)
        assertEquals(scheme.outlineVariant, c.tooltipBorder)
        assertEquals(scheme.outlineVariant, c.bar)
        assertEquals(app.zones, c.zones)
    }

    @Test
    fun `custom brand flows through`() {
        val blue = Brand(accent = Color(0xFF2E6BE8))
        val blueScheme = blue.toColorScheme()
        val b = chartColors(blueScheme, blue.toAppColors())
        assertEquals(blueScheme.primary, b.accent)
        assertNotEquals(c.accent, b.accent)
        assertNotEquals(c.zones, b.zones)
        assertEquals(blue.toAppColors().accentTint, b.goalFill)
    }

    @Test
    fun `zoneColor clamps and survives empty ramp`() {
        assertEquals(app.zones[0], c.zoneColor(1))
        assertEquals(app.zones[4], c.zoneColor(5))
        assertEquals(app.zones[0], c.zoneColor(0))
        assertEquals(app.zones[4], c.zoneColor(9))
        assertEquals(c.line, c.copy(zones = emptyList()).zoneColor(3))
    }
}
