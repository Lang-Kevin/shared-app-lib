package com.kevin.shared.ui.media

import com.kevin.shared.ui.theme.Brand
import com.kevin.shared.ui.theme.toAppColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraHudTest {

    @Test
    fun idle_showsIdleControls_notReadings() {
        assertEquals(HudSlots(idleControls = true, readings = false), hudSlots(recording = false))
    }

    @Test
    fun recording_showsReadings_hidesIdleControls() {
        // §7.3 A2: no resolution chip and no camera switch while recording.
        assertEquals(HudSlots(idleControls = false, readings = true), hudSlots(recording = true))
    }

    @Test
    fun scrimPeak_neverAbove25Percent() {
        val tokenAlpha = Brand.Default.toAppColors().mediaScrim.alpha
        assertTrue(hudScrimPeakAlpha(tokenAlpha) <= HudScrimMaxAlpha + 0.005f)
        assertEquals(0.10f, hudScrimPeakAlpha(0.10f), 0f)
    }

    @Test
    fun scrim_coversBottomFifth() {
        assertEquals(0.20f, HudScrimHeightFraction, 0f)
    }
}
