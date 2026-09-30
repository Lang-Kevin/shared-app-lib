package com.kevin.shared.ui.chart

fun aggregateByChunks(values: List<Float>, maxPoints: Int = 300): List<Float> {
    if (values.size <= maxPoints) return values
    val chunkSize = (values.size + maxPoints - 1) / maxPoints
    return values.chunked(chunkSize) { chunk -> chunk.max() }
}
