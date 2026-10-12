package com.kevin.shared.ui.overlay

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TutorialSpotlightTest {
    private val screen = Size(1000f, 2000f)

    @Test
    fun toOverlaySpace_subtractsOverlayOrigin() {
        // Overlay starts 80px below the root (e.g. under a top bar): the hole must move up by 80.
        assertEquals(Rect(100f, 220f, 200f, 320f), toOverlaySpace(Rect(100f, 300f, 200f, 400f), Offset(0f, 80f)))
    }

    @Test
    fun spotlightHole_padsAndClampsToBounds() {
        assertEquals(Rect(92f, 292f, 208f, 408f), spotlightHole(Rect(100f, 300f, 200f, 400f), 8f, screen))
        assertEquals(Rect(0f, 0f, 58f, 58f), spotlightHole(Rect(4f, 4f, 50f, 50f), 8f, screen))
    }

    @Test
    fun spotlightHole_offscreenTarget_isNull() {
        assertNull(spotlightHole(Rect(100f, -200f, 200f, -100f), 8f, screen))
        assertNull(spotlightHole(Rect(1200f, 300f, 1300f, 400f), 8f, screen))
    }

    @Test
    fun card_noHole_goesToBottom() {
        assertFalse(spotlightCardAtTop(null, 2000f, 400f, 32f))
    }

    @Test
    fun card_prefersBelow_thenAbove() {
        assertFalse(spotlightCardAtTop(Rect(0f, 100f, 100f, 300f), 2000f, 400f, 32f))
        assertTrue(spotlightCardAtTop(Rect(0f, 1700f, 100f, 1900f), 2000f, 400f, 32f))
    }

    @Test
    fun card_fitsNeitherSide_takesTheRoomierOne() {
        // Above 900 vs below 500: top.
        assertTrue(spotlightCardAtTop(Rect(0f, 900f, 100f, 1500f), 2000f, 1200f, 32f))
        // Above 300 vs below 1000: bottom.
        assertFalse(spotlightCardAtTop(Rect(0f, 300f, 100f, 1000f), 2000f, 1200f, 32f))
    }

    @Test
    fun nextStepIndex_advancesThenFinishes() {
        assertEquals(1, nextStepIndex(0, 3))
        assertEquals(2, nextStepIndex(1, 3))
        assertNull(nextStepIndex(2, 3))
        assertNull(nextStepIndex(0, 1))
    }
}
