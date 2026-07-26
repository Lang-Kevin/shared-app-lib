package com.kevin.shared.ui.chart

data class VelocityPoint(val timestampMs: Long, val value: Float)

data class AggregatedPoint(
    val timeSec: Long,
    val min: Float,
    val avg: Float,
    val max: Float,
)
