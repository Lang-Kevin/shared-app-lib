package com.kevin.shared.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val AUTO_FIT_THRESHOLD = 300

@Composable
fun VelocityChart(
    values: List<Float>,
    currentValue: Float? = null,
    mode: ChartScaleMode = ChartScaleMode.SCROLL,
    fullHistory: List<VelocityPoint> = emptyList(),
    lineColor: Color = Color(0xFF7C4DFF),
    markerColor: Color = Color.White,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val displayValues: List<Float> = when (mode) {
            ChartScaleMode.SCROLL -> values
            ChartScaleMode.SHOW_ALL -> {
                if (fullHistory.isEmpty()) values
                else aggregatePerSecond(fullHistory).map { it.max }
            }
            ChartScaleMode.AUTO_FIT -> {
                if (fullHistory.isEmpty()) values
                else if (fullHistory.size <= AUTO_FIT_THRESHOLD) fullHistory.map { it.value }
                else aggregatePerSecond(fullHistory).map { it.max }
            }
        }

        if (displayValues.size < 2) return@Canvas

        val leftPaddingPx = with(density) { 54.dp.toPx() }
        val chartWidth = size.width - leftPaddingPx

        val dataMin = displayValues.min()
        val dataMax = displayValues.max()
        val pad = ((dataMax - dataMin) * 0.1f).coerceAtLeast(0.5f)
        val yMin = dataMin - pad
        val yMax = dataMax + pad
        val yRange = (yMax - yMin).coerceAtLeast(0.01f)

        fun velocityToY(v: Float): Float = size.height * (1f - (v - yMin) / yRange)

        val labelPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { 10.sp.toPx() }
            color = android.graphics.Color.argb(160, 255, 255, 255)
        }

        listOf(yMin, (yMin + yMax) / 2f, yMax).forEach { tick ->
            val y = velocityToY(tick)
            drawLine(
                color = Color.White.copy(alpha = 0.10f),
                start = Offset(leftPaddingPx, y),
                end = Offset(size.width, y),
                strokeWidth = with(density) { 1.dp.toPx() },
            )
            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(tick),
                4f,
                y + labelPaint.textSize / 3f,
                labelPaint,
            )
        }

        val path = Path()
        displayValues.forEachIndexed { index, v ->
            val x = leftPaddingPx + (index.toFloat() / (displayValues.size - 1)) * chartWidth
            val y = velocityToY(v.coerceIn(yMin, yMax))
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = markerColor,
            style = Stroke(
                width = with(density) { 2.dp.toPx() },
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )

        val lastVal = currentValue ?: displayValues.last()
        val lastX = leftPaddingPx + chartWidth
        val lastY = velocityToY(lastVal.coerceIn(yMin, yMax))

        drawCircle(
            color = lineColor,
            radius = with(density) { 4.dp.toPx() },
            center = Offset(lastX, lastY),
        )

        val valuePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            textSize = with(density) { 11.sp.toPx() }
            color = lineColor.toArgb()
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        drawContext.canvas.nativeCanvas.drawText(
            "%.2f m/s".format(lastVal),
            size.width - with(density) { 2.dp.toPx() },
            lastY - with(density) { 8.dp.toPx() },
            valuePaint,
        )
    }
}
