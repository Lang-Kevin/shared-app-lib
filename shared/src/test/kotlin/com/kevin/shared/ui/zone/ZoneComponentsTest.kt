package com.kevin.shared.ui.zone

import org.junit.Assert.assertEquals
import org.junit.Test

class ZoneComponentsTest {
    @Test fun targetZoneStripIsOpaqueOthersMuted() {
        assertEquals(1f, zoneStripAlpha(zone = 3, targetZone = 3), 0f)
        assertEquals(0.35f, zoneStripAlpha(zone = 2, targetZone = 3), 0f)
        assertEquals(0.35f, zoneStripAlpha(zone = 1, targetZone = 0), 0f)
    }
}
