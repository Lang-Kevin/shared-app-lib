package com.kevin.shared.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChartAggregatorTest {

    @Test
    fun `aggregatePerSecond empty list returns empty`() {
        assertTrue(aggregatePerSecond(emptyList()).isEmpty())
    }

    @Test
    fun `aggregatePerSecond single point forms one bucket`() {
        val result = aggregatePerSecond(listOf(VelocityPoint(1000L, 3.0f)))
        assertEquals(1, result.size)
        assertEquals(1L, result[0].timeSec)
        assertEquals(3.0f, result[0].max, 0.001f)
        assertEquals(3.0f, result[0].min, 0.001f)
        assertEquals(3.0f, result[0].avg, 0.001f)
    }

    @Test
    fun `aggregatePerSecond picks max within bucket`() {
        val points = listOf(
            VelocityPoint(1000L, 1.0f),
            VelocityPoint(1200L, 5.0f),
            VelocityPoint(1800L, 3.0f),
        )
        val result = aggregatePerSecond(points)
        assertEquals(1, result.size)
        assertEquals(5.0f, result[0].max, 0.001f)
        assertEquals(1.0f, result[0].min, 0.001f)
    }

    @Test
    fun `aggregatePerSecond splits across seconds correctly`() {
        val points = listOf(
            VelocityPoint(0L, 2.0f),
            VelocityPoint(500L, 4.0f),
            VelocityPoint(1000L, 6.0f),
            VelocityPoint(1500L, 1.0f),
        )
        val result = aggregatePerSecond(points)
        assertEquals(2, result.size)
        assertEquals(0L, result[0].timeSec)
        assertEquals(4.0f, result[0].max, 0.001f)
        assertEquals(1L, result[1].timeSec)
        assertEquals(6.0f, result[1].max, 0.001f)
    }

    @Test
    fun `aggregatePerSecond result is sorted by time`() {
        val points = listOf(
            VelocityPoint(2000L, 1.0f),
            VelocityPoint(0L, 2.0f),
            VelocityPoint(1000L, 3.0f),
        )
        val result = aggregatePerSecond(points)
        assertEquals(listOf(0L, 1L, 2L), result.map { it.timeSec })
    }

    @Test
    fun `aggregateByChunks returns original when under threshold`() {
        val values = listOf(1.0f, 2.0f, 3.0f)
        assertEquals(values, aggregateByChunks(values, maxPoints = 300))
    }

    @Test
    fun `aggregateByChunks reduces to maxPoints using max`() {
        val values = (1..600).map { it.toFloat() }
        val result = aggregateByChunks(values, maxPoints = 300)
        assertTrue(result.size <= 300)
        assertEquals(2.0f, result[0], 0.001f)
    }
}
