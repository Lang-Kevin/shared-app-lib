package com.kevin.shared.ui.chart

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText

/** Polyline through [points] (px). A NaN coordinate breaks the line; an isolated point becomes a dot. */
internal fun DrawScope.drawPolyline(points: List<Offset>, color: Color, strokePx: Float) {
    val path = Path()
    var run = 0
    var single = Offset.Zero
    for (p in points) {
        if (p.x.isNaN() || p.y.isNaN()) {
            if (run == 1) drawCircle(color, strokePx, single)
            run = 0
            continue
        }
        if (run == 0) {
            path.moveTo(p.x, p.y)
            single = p
        } else {
            path.lineTo(p.x, p.y)
        }
        run++
    }
    if (run == 1) drawCircle(color, strokePx, single)
    drawPath(path, color, style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Horizontal line; [dash] = on/off lengths in px, null = solid. */
internal fun DrawScope.drawHLine(
    y: Float, startX: Float, endX: Float, color: Color, strokePx: Float, dash: FloatArray? = null,
) {
    drawLine(color, Offset(startX, y), Offset(endX, y), strokePx, pathEffect = dash?.let { PathEffect.dashPathEffect(it) })
}

/** Vertical line; [dash] = on/off lengths in px, null = solid. */
internal fun DrawScope.drawVLine(
    x: Float, startY: Float, endY: Float, color: Color, strokePx: Float, dash: FloatArray? = null,
) {
    drawLine(color, Offset(x, startY), Offset(x, endY), strokePx, pathEffect = dash?.let { PathEffect.dashPathEffect(it) })
}

/** Outer size of a pill around [text]. */
internal fun pillSize(text: TextLayoutResult, padH: Float, padV: Float): Size =
    Size(text.size.width + 2f * padH, text.size.height + 2f * padV)

/** Fully rounded pill at [topLeft] with optional border (stroke inside the outer bounds). */
internal fun DrawScope.drawPill(
    text: TextLayoutResult,
    topLeft: Offset,
    padH: Float,
    padV: Float,
    fill: Color,
    textColor: Color,
    border: Color? = null,
    borderPx: Float = 0f,
) {
    val size = pillSize(text, padH, padV)
    drawRoundRect(fill, topLeft, size, CornerRadius(size.height / 2f))
    if (border != null && borderPx > 0f) {
        val half = borderPx / 2f
        val inner = Size(size.width - borderPx, size.height - borderPx)
        drawRoundRect(border, topLeft + Offset(half, half), inner, CornerRadius(inner.height / 2f), style = Stroke(borderPx))
    }
    drawText(text, textColor, topLeft + Offset(padH, padV))
}

/** Bar with rounded top corners only (weekly load bars). */
internal fun DrawScope.drawTopRoundedRect(rect: BarRect, radiusPx: Float, color: Color, style: DrawStyle = Fill) {
    if (rect.width <= 0f || rect.height <= 0f) return
    val r = minOf(radiusPx, rect.width / 2f, rect.height)
    val cr = CornerRadius(r, r)
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = rect.left, top = rect.top, right = rect.left + rect.width, bottom = rect.top + rect.height,
                topLeftCornerRadius = cr, topRightCornerRadius = cr,
                bottomRightCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero,
            ),
        )
    }
    drawPath(path, color, style = style)
}
