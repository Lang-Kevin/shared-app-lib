package com.kevin.shared.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartGeometryTest {

    private val eps = 1e-3f

    @Test
    fun `plotFraction maps and clamps`() {
        assertEquals(0.25f, plotFraction(50f, 0f, 200f), eps)
        assertEquals(0.5f, plotFraction(60f, 20f, 80f), eps)
        assertEquals(0f, plotFraction(-10f, 0f, 200f), eps)
        assertEquals(1f, plotFraction(300f, 0f, 200f), eps)
        assertEquals(0f, plotFraction(50f, 0f, 0f), eps)
        assertEquals(0f, plotFraction(Float.NaN, 0f, 200f), eps)
    }

    @Test
    fun `xToPx maps window and centers zero span`() {
        assertEquals(200f, xToPx(150f, 100f..200f, 0f, 400f), eps)
        assertEquals(10f, xToPx(100f, 100f..200f, 10f, 400f), eps)
        assertEquals(200f, xToPx(5f, 5f..5f, 0f, 400f), eps)
    }

    @Test
    fun `yToPx inverts and centers flat range`() {
        assertEquals(0f, yToPx(10f, 0f..10f, 0f, 100f), eps)
        assertEquals(100f, yToPx(0f, 0f..10f, 0f, 100f), eps)
        assertEquals(70f, yToPx(5f, 5f..5f, 20f, 100f), eps)
    }

    @Test
    fun `fractionToX clamps into window`() {
        assertEquals(150f, fractionToX(0.5f, 100f..200f), eps)
        assertEquals(200f, fractionToX(3f, 100f..200f), eps)
    }

    @Test
    fun `nearestIndexByX picks nearest and earlier on tie`() {
        val xs = listOf(0f, 10f, 20f, 30f)
        val at: (Int) -> Float = { xs[it] }
        assertEquals(1, nearestIndexByX(4, 14f, at))
        assertEquals(1, nearestIndexByX(4, 15f, at))
        assertEquals(2, nearestIndexByX(4, 16f, at))
        assertEquals(0, nearestIndexByX(4, -5f, at))
        assertEquals(3, nearestIndexByX(4, 99f, at))
    }

    @Test
    fun `nearestIndexByX empty NaN single`() {
        assertNull(nearestIndexByX(0, 1f) { 0f })
        assertNull(nearestIndexByX(3, Float.NaN) { it.toFloat() })
        assertEquals(0, nearestIndexByX(1, 50f) { 7f })
    }

    @Test
    fun `tooltip centers and clamps`() {
        assertEquals(40f, tooltipLeft(50f, 20f, 0f, 100f), eps)
        assertEquals(0f, tooltipLeft(5f, 20f, 0f, 100f), eps)
        assertEquals(80f, tooltipLeft(98f, 20f, 0f, 100f), eps)
    }

    @Test
    fun `tooltip wider than plot pins left`() {
        assertEquals(0f, tooltipLeft(50f, 150f, 0f, 100f), eps)
    }

    @Test
    fun `trendX spreads and centers single`() {
        assertEquals(50f, trendX(0, 1, 0f, 100f), eps)
        assertEquals(0f, trendX(0, 3, 0f, 100f), eps)
        assertEquals(50f, trendX(1, 3, 0f, 100f), eps)
        assertEquals(104f, trendX(2, 3, 4f, 100f), eps)
    }

    @Test
    fun `zone segments skip zero zones and gap only between drawn`() {
        val s = zoneSegments(listOf(60L, 0L, 30L, 0L, 10L), 100f, 2f)
        assertEquals(listOf(0, 2, 4), s.map { it.index })
        assertEquals(0f, s[0].left, eps); assertEquals(57.6f, s[0].width, eps)
        assertEquals(59.6f, s[1].left, eps); assertEquals(28.8f, s[1].width, eps)
        assertEquals(90.4f, s[2].left, eps); assertEquals(9.6f, s[2].width, eps)
    }

    @Test
    fun `zone segments single zone fills width`() {
        assertEquals(listOf(Segment(2, 0f, 100f)), zoneSegments(listOf(0L, 0L, 50L, 0L, 0L), 100f, 2f))
    }

    @Test
    fun `zone segments equal durations equal widths`() {
        val s = zoneSegments(listOf(5L, 5L), 102f, 2f)
        assertEquals(50f, s[0].width, eps)
        assertEquals(50f, s[1].width, eps)
    }

    @Test
    fun `zone segments drop gap when too large`() {
        val s = zoneSegments(listOf(1L, 1L), 1f, 2f)
        assertEquals(0.5f, s[0].width, eps)
        assertEquals(0.5f, s[1].left, eps)
    }

    @Test
    fun `zone segments empty all zero negative zero width`() {
        assertTrue(zoneSegments(emptyList(), 100f, 2f).isEmpty())
        assertTrue(zoneSegments(listOf(0L, 0L), 100f, 2f).isEmpty())
        assertTrue(zoneSegments(listOf(-5L), 100f, 2f).isEmpty())
        assertTrue(zoneSegments(listOf(5L), 0f, 2f).isEmpty())
    }

    @Test
    fun `bars scale to max with gaps`() {
        val b = barGeometry(listOf(1f, 2f, 4f), 100f, 40f, 8f)
        assertEquals(listOf(BarRect(0f, 30f, 28f, 10f), BarRect(36f, 20f, 28f, 20f), BarRect(72f, 0f, 28f, 40f)), b)
    }

    @Test
    fun `bars equal values are full height`() {
        assertTrue(barGeometry(listOf(3f, 3f), 100f, 40f, 8f).all { it.height == 40f && it.top == 0f })
    }

    @Test
    fun `bars zero NaN negative have zero height`() {
        val b = barGeometry(listOf(0f, Float.NaN, -2f, 2f), 100f, 40f, 0f)
        assertEquals(listOf(0f, 0f, 0f, 40f), b.map { it.height })
        assertTrue(barGeometry(listOf(0f, 0f), 100f, 40f, 8f).all { it.height == 0f && it.top == 40f })
    }

    @Test
    fun `bars empty and gap overflow`() {
        assertTrue(barGeometry(emptyList(), 100f, 40f, 8f).isEmpty())
        val b = barGeometry(listOf(1f, 1f), 10f, 40f, 20f)
        assertEquals(5f, b[0].width, eps)
        assertEquals(5f, b[1].left, eps)
    }
}
