package com.kevin.shared.ui.media

import org.junit.Assert.assertEquals
import org.junit.Test

class FramingGuideTest {

    @Test
    fun alpha_fullWhenIdle_dimmedWhileRecording() {
        assertEquals(1f, FramingGuideDefaults.alpha(recording = false), 0f)
        assertEquals(0.55f, FramingGuideDefaults.alpha(recording = true), 0f)
    }

    @Test
    fun alpha_outOfFrameStaysFull_evenWhileRecording() {
        assertEquals(1f, FramingGuideDefaults.alpha(recording = true, outOfFrame = true), 0f)
        assertEquals(1f, FramingGuideDefaults.alpha(recording = false, outOfFrame = true), 0f)
    }

    @Test
    fun geometry_jumpConstants_onPortraitFrame() {
        val g = framingGeometry(1000f, 2000f, 0.83f, 0.10f, 0.5f, armPx = 60f)
        assertEquals(1660f, g.footY, 0.01f)
        assertEquals(250f, g.roiLeft, 0.01f)
        assertEquals(750f, g.roiRight, 0.01f)
        assertEquals(200f, g.roiTop, 0.01f)
        assertEquals(60f, g.arm, 0.01f)
    }

    @Test
    fun geometry_tinyFrame_armShrinksToHalfRoiWidth() {
        val g = framingGeometry(80f, 100f, 0.83f, 0.10f, 0.5f, armPx = 60f)
        assertEquals(20f, g.arm, 0.01f)
    }

    @Test
    fun geometry_clampsFractions() {
        val g = framingGeometry(1000f, 2000f, 1.4f, -0.1f, 2f, armPx = 60f)
        assertEquals(2000f, g.footY, 0.01f)
        assertEquals(0f, g.roiTop, 0.01f)
        assertEquals(0f, g.roiLeft, 0.01f)
        assertEquals(1000f, g.roiRight, 0.01f)
    }

    @Test
    fun hint_sitsBelowFootLine_whenItFits() {
        assertEquals(1676f, framingHintTop(footY = 1660f, gap = 16f, hintHeight = 80f, bottomLimit = 1800f), 0.01f)
    }

    @Test
    fun hint_flipsAboveFootLine_whenItWouldHitTheBand() {
        assertEquals(1564f, framingHintTop(footY = 1660f, gap = 16f, hintHeight = 80f, bottomLimit = 1680f), 0.01f)
    }

    @Test
    fun hint_flippedNeverLeavesTheTop() {
        assertEquals(0f, framingHintTop(footY = 40f, gap = 16f, hintHeight = 80f, bottomLimit = 60f), 0.01f)
    }
}
