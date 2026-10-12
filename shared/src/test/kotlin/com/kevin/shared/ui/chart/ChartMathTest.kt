package com.kevin.shared.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartMathTest {

    private val zones = listOf(
        ChartZone(1, 93f, 111f, "Z1"), ChartZone(2, 111f, 130f, "Z2"), ChartZone(3, 130f, 148f, "Z3"),
        ChartZone(4, 148f, 167f, "Z4"), ChartZone(5, 167f, 185f, "Z5"),
    )
    private val nan = Float.NaN

    private fun assertRange(lo: Float, hi: Float, r: ClosedFloatingPointRange<Float>) {
        assertEquals(lo, r.start, 1e-3f)
        assertEquals(hi, r.endInclusive, 1e-3f)
    }

    // ---- LineSeries ----

    @Test
    fun `series x defaults to index`() {
        val s = LineSeries(listOf(5f, 6f))
        assertEquals(1f, s.xAt(1), 0f)
        assertEquals(2, s.size)
        assertEquals(30f, LineSeries(listOf(5f, 6f), listOf(0f, 30f)).xAt(1), 0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `series rejects mismatched xs`() {
        LineSeries(listOf(1f, 2f), listOf(0f))
    }

    // ---- zoneChartRange ----

    @Test
    fun `static range pads zones by 8`() {
        assertRange(85f, 193f, zoneChartRange(listOf(120f, 140f), zones, 3, dynamic = false))
    }

    @Test
    fun `static range widens to include data outside zones`() {
        assertRange(85f, 200f, zoneChartRange(listOf(120f, 200f), zones, null, dynamic = false))
    }

    @Test
    fun `static without zones falls back to data fit`() {
        assertRange(98f, 122f, zoneChartRange(listOf(100f, 120f), emptyList(), null, dynamic = false))
    }

    @Test
    fun `dynamic includes target zone bounds`() {
        assertRange(95.2f, 152.8f, zoneChartRange(listOf(100f, 120f), zones, 3, dynamic = true))
    }

    @Test
    fun `dynamic all equal pads at least one`() {
        assertRange(139f, 141f, zoneChartRange(listOf(140f, 140f), zones, null, dynamic = true))
    }

    @Test
    fun `dynamic ignores NaN`() {
        assertRange(99f, 111f, zoneChartRange(listOf(nan, 100f, 110f), zones, null, dynamic = true))
    }

    @Test
    fun `dynamic empty or all NaN without target is unit range`() {
        assertRange(0f, 1f, zoneChartRange(emptyList(), zones, null, dynamic = true))
        assertRange(0f, 1f, zoneChartRange(listOf(nan, nan), emptyList(), null, dynamic = true))
    }

    @Test
    fun `dynamic empty with target fits target`() {
        assertRange(128.2f, 149.8f, zoneChartRange(emptyList(), zones, 3, dynamic = true))
    }

    // ---- trendRange ----

    @Test
    fun `trend pads ten percent`() {
        assertRange(0.8f, 3.2f, trendRange(listOf(1f, 2f, 3f)))
    }

    @Test
    fun `trend flat pads by max of one and value`() {
        assertRange(4.5f, 5.5f, trendRange(listOf(5f, 5f)))
        assertRange(0.1f, 0.3f, trendRange(listOf(0.2f)))
    }

    @Test
    fun `trend includes band and reference`() {
        assertRange(0.75f, 1.35f, trendRange(listOf(1.0f, 1.1f), band = 0.8f..1.3f))
        assertRange(-0.2f, 2.2f, trendRange(listOf(0f, 1f), reference = 2f))
    }

    @Test
    fun `trend empty or NaN is unit range`() {
        assertRange(0f, 1f, trendRange(emptyList()))
        assertRange(0f, 1f, trendRange(listOf(nan)))
    }

    // ---- visibleIndices ----

    private val idx: (Int) -> Float = { it.toFloat() }

    @Test
    fun `visible slice adds one neighbour each side`() {
        assertEquals(2..6, visibleIndices(10, 2.5f, 5.5f, idx))
        assertEquals(1..6, visibleIndices(10, 2f, 5f, idx))
    }

    @Test
    fun `visible slice between two samples keeps both`() {
        assertEquals(3..4, visibleIndices(10, 3.2f, 3.8f, idx))
    }

    @Test
    fun `visible slice outside data clamps to edge sample`() {
        assertEquals(9..9, visibleIndices(10, 20f, 30f, idx))
        assertEquals(0..0, visibleIndices(10, -10f, -5f, idx))
    }

    @Test
    fun `visible slice with seconds xs`() {
        val xs = listOf(0f, 0.5f, 1f, 1.5f, 2f)
        assertEquals(1..3, visibleIndices(xs.size, 0.6f, 1.4f) { xs[it] })
    }

    @Test
    fun `visible slice empty and invalid`() {
        assertTrue(visibleIndices(0, 0f, 1f, idx).isEmpty())
        assertTrue(visibleIndices(10, nan, 5f, idx).isEmpty())
        assertTrue(visibleIndices(10, 5f, 2f, idx).isEmpty())
        assertEquals(0..0, visibleIndices(1, 0f, 0f, idx))
    }

    // ---- decimate ----

    private val ys1000: (Int) -> Float = { it.toFloat() }

    @Test
    fun `decimate keeps small ranges raw`() {
        val ys = listOf(1f, nan, 3f)
        val pts = decimate(0..2, 600, idx, { ys[it] })
        assertEquals(3, pts.size)
        assertTrue(pts[1].y.isNaN())
        assertEquals(ChartPoint(2f, 3f), pts[2])
    }

    @Test
    fun `decimate bounds point count and keeps last raw`() {
        val pts = decimate(0..999, 100, idx, ys1000)
        assertTrue("size ${pts.size}", pts.size <= 102)
        assertEquals(ChartPoint(4.5f, 4.5f), pts.first())
        assertEquals(ChartPoint(999f, 999f), pts.last())
    }

    @Test
    fun `decimate chunks are anchored at absolute indices`() {
        val a = decimate(0..999, 100, idx, ys1000)
        val b = decimate(3..1002, 100, idx, ys1000)
        assertTrue(a.contains(ChartPoint(14.5f, 14.5f)))
        assertTrue(b.contains(ChartPoint(14.5f, 14.5f)))
    }

    @Test
    fun `decimate turns chunk with NaN into gap`() {
        val pts = decimate(0..999, 100, idx, { if (it == 55) nan else it.toFloat() })
        val gap = pts.single { it.x == 54.5f }
        assertTrue(gap.y.isNaN())
        assertEquals(1, pts.count { it.y.isNaN() })
    }

    @Test
    fun `decimate empty and single`() {
        assertTrue(decimate(IntRange.EMPTY, 600, idx, ys1000).isEmpty())
        assertEquals(listOf(ChartPoint(5f, 5f)), decimate(5..5, 600, idx, ys1000))
    }

    private val spiky: (Int) -> Float = { if (it == 505) 200f else if (it == 503) -50f else 100f }

    @Test
    fun `decimate default mode equals Mean`() {
        assertEquals(decimate(0..999, 100, idx, spiky), decimate(0..999, 100, idx, spiky, DecimationMode.Mean))
    }

    @Test
    fun `decimate Max keeps bucket peak`() {
        val pts = decimate(0..999, 100, idx, spiky, DecimationMode.Max)
        assertTrue(pts.contains(ChartPoint(505f, 200f)))
        assertTrue(pts.size <= 102)
        assertEquals(ChartPoint(999f, 999f).x, pts.last().x)
    }

    @Test
    fun `decimate MinMax keeps both extremes in time order`() {
        val pts = decimate(0..999, 100, idx, spiky, DecimationMode.MinMax)
        val lo = pts.indexOf(ChartPoint(503f, -50f))
        val hi = pts.indexOf(ChartPoint(505f, 200f))
        assertTrue(lo >= 0 && hi == lo + 1)
        assertTrue("size ${pts.size}", pts.size <= 103)
        assertTrue(pts.zipWithNext().all { (p, q) -> p.x <= q.x })
    }

    @Test
    fun `decimate Max and MinMax preserve gaps`() {
        for (m in listOf(DecimationMode.Max, DecimationMode.MinMax)) {
            val pts = decimate(0..999, 100, idx, { if (it == 55) nan else it.toFloat() }, m)
            assertEquals(1, pts.count { it.y.isNaN() })
        }
    }

    // ---- lastFiniteIndex ----

    @Test
    fun `last finite index skips trailing NaN`() {
        val ys = listOf(1f, 2f, nan, nan)
        assertEquals(1, lastFiniteIndex(ys.size) { ys[it] })
        assertNull(lastFiniteIndex(2) { nan })
        assertNull(lastFiniteIndex(0) { 0f })
    }
}
