package com.kevin.shared.ui.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SegmentedControlTest {
    private val t = SegmentTokens(
        accent = Color.Red, accentTint = Color.DarkGray, onSurface = Color.White,
        onSurfaceVariant = Color.Gray, textDisabled = Color.LightGray, outlineVariant = Color.Blue,
    )

    @Test fun selectedSegmentIsAccentBorderPlusTint() {
        assertEquals(SegmentStyle(Color.DarkGray, Color.White, Color.Red), segmentStyle(true, true, t))
    }

    @Test fun unselectedSegmentIsPlain() {
        assertEquals(SegmentStyle(Color.Transparent, Color.Gray, Color.Transparent), segmentStyle(false, true, t))
    }

    @Test fun disabledNeverShowsTheAccent() {
        assertEquals(SegmentStyle(Color.Transparent, Color.Gray, Color.Blue), segmentStyle(true, false, t))
        assertEquals(SegmentStyle(Color.Transparent, Color.LightGray, Color.Transparent), segmentStyle(false, false, t))
    }

    @Test fun dividersHideNextToTheSelectedSegment() {
        // 4 segments, dividers 0..2; selected = 1 -> dividers 0 and 1 touch it
        assertFalse(segmentDividerVisible(0, 1))
        assertFalse(segmentDividerVisible(1, 1))
        assertTrue(segmentDividerVisible(2, 1))
        // nothing selected (-1) -> all visible
        assertTrue(segmentDividerVisible(0, -1))
    }

    @Test fun tabTextIsWhiteOnlyWhenSelected() {
        assertEquals(Color.White, tabContentColor(true, t))
        assertEquals(Color.LightGray, tabContentColor(false, t))
    }
}
