package com.kevin.shared.ui.chart

fun aggregatePerSecond(points: List<VelocityPoint>): List<AggregatedPoint> {
    if (points.isEmpty()) return emptyList()
    return points
        .groupBy { it.timestampMs / 1000L }
        .entries
        .sortedBy { it.key }
        .map { (sec, bucket) ->
            val values = bucket.map { it.value }
            AggregatedPoint(
                timeSec = sec,
                min = values.min(),
                avg = values.average().toFloat(),
                max = values.max(),
            )
        }
}

fun aggregateByChunks(values: List<Float>, maxPoints: Int = 300): List<Float> {
    if (values.size <= maxPoints) return values
    val chunkSize = (values.size + maxPoints - 1) / maxPoints
    return values.chunked(chunkSize) { chunk -> chunk.max() }
}
