package com.kevin.shared.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class InlineBarTest {
    @Test fun fractionIsClampedToUnitRange() {
        assertEquals(0f, inlineBarFraction(-0.5f), 0f)
        assertEquals(0.4f, inlineBarFraction(0.4f), 0f)
        assertEquals(1f, inlineBarFraction(2f), 0f)
    }
}
