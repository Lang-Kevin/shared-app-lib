package com.kevin.shared.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Test

class ChartWindowTest {

    private fun assertRange(lo: Float, hi: Float, r: ClosedFloatingPointRange<Float>) {
        assertEquals(lo, r.start, 1e-4f)
        assertEquals(hi, r.endInclusive, 1e-4f)
    }

    @Test
    fun `viewport toWindow full and zoomed`() {
        assertRange(0f, 600f, ChartViewport.Full.toWindow(600f))
        assertRange(100f, 200f, ChartViewport(0.25f, 0.5f).toWindow(400f))
    }

    @Test
    fun `viewport toWindow with invalid total is empty at 0`() {
        assertRange(0f, 0f, ChartViewport(0.25f, 0.5f).toWindow(-5f))
        assertRange(0f, 0f, ChartViewport.Full.toWindow(Float.NaN))
        assertRange(0f, 0f, ChartViewport.Full.toWindow(Float.POSITIVE_INFINITY))
    }

    @Test
    fun `live toWindow follows visibleRange`() {
        assertRange(380f, 499f, LiveWindow().toWindow(500))
        assertRange(330f, 449f, LiveWindow(120f, 449f).toWindow(500))
    }

    @Test
    fun `live toWindow empty and single`() {
        assertRange(0f, 0f, LiveWindow().toWindow(0))
        assertRange(0f, 0f, LiveWindow().toWindow(1))
    }

    @Test
    fun `live zoom honours custom minWindow`() {
        assertEquals(10f, LiveWindow().zoomBy(100f, 0.5f, 1000, minWindow = 10f).windowSeconds, 1e-3f)
    }

    @Test
    fun `live zoom out honours custom maxWindow`() {
        assertEquals(600f, LiveWindow(100f, null).zoomBy(0.01f, 0.5f, 50, maxWindow = 600f).windowSeconds, 1e-3f)
    }

    @Test
    fun `live zoom defaults keep hr limits`() {
        assertEquals(LiveWindow.MAX_WINDOW, LiveWindow().zoomBy(0.001f, 0.5f, 50).windowSeconds, 1e-3f)
        assertEquals(LiveWindow.MIN.toFloat(), LiveWindow().zoomBy(100f, 0.5f, 1000).windowSeconds, 1e-3f)
    }
}
