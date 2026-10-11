package com.kevin.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class TokensTest {
    @Test fun spacingScale() {
        val s = AppSpacing()
        assertEquals(listOf(2.dp, 4.dp, 8.dp, 12.dp, 16.dp, 24.dp, 32.dp),
            listOf(s.xxs, s.xs, s.sm, s.md, s.lg, s.xl, s.xxl))
    }

    @Test fun shapeScale() {
        assertEquals(RoundedCornerShape(8.dp), AppShapes.small)
        assertEquals(RoundedCornerShape(14.dp), AppShapes.medium)
        assertEquals(RoundedCornerShape(20.dp), AppShapes.large)
        assertEquals(RoundedCornerShape(50), PillShape)
    }
}
