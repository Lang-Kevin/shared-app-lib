package com.kevin.shared.ui.media

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppShapes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShutterButtonTest {

    @Test
    fun glyphSize_idleDisc_recordingSquare() {
        assertEquals(56.dp, shutterGlyphSize(recording = false))
        assertEquals(24.dp, shutterGlyphSize(recording = true))
    }

    @Test
    fun glyphs_fitInsideRing() {
        val inner = ShutterDefaults.Size - ShutterDefaults.RingWidth * 2
        assertTrue(shutterGlyphSize(recording = false) < inner)
        assertTrue(shutterGlyphSize(recording = true) < inner)
    }

    @Test
    fun sweep_nullWhenIdleOrNoProgress() {
        assertNull(shutterSweep(recording = false, progress = 0.5f))
        assertNull(shutterSweep(recording = true, progress = null))
        assertNull(shutterSweep(recording = true, progress = Float.NaN))
    }

    @Test
    fun sweep_clampedToFullCircle() {
        assertEquals(180f, shutterSweep(recording = true, progress = 0.5f)!!, 0.001f)
        assertEquals(360f, shutterSweep(recording = true, progress = 1.7f)!!, 0.001f)
        assertEquals(0f, shutterSweep(recording = true, progress = -0.2f)!!, 0.001f)
    }

    @Test
    fun stopGlyphShape_isFourDpExtraSmall() {
        // The stop square uses MaterialTheme.shapes.extraSmall. If SP1 ever overrides it, re-check the glyph.
        assertEquals(RoundedCornerShape(4.dp), AppShapes.extraSmall)
    }
}
