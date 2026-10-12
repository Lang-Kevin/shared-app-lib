package com.kevin.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.kevin.shared.format.NO_VALUE
import com.kevin.shared.ui.theme.AppTheme

/** Unit suffix size relative to the (autosized) value. */
internal val UnitScale: TextUnit = 0.55.em

internal fun metricValueText(value: String, unit: String?, unitColor: Color): AnnotatedString = buildAnnotatedString {
    append(value)
    if (!unit.isNullOrEmpty() && value != NO_VALUE) {
        withStyle(SpanStyle(color = unitColor, fontSize = UnitScale)) { // design-ignore: unit suffix scales with the autosized value
            append(" ")
            append(unit)
        }
    }
}

/** Label over value (+ optional small unit): the borderless core of StatItem and StatGrid cells. */
@Composable
fun Metric(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    valueColor: Color = Color.Unspecified,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = metricValueText(value, unit, MaterialTheme.colorScheme.onSurfaceVariant),
            style = MaterialTheme.typography.headlineSmall,
            color = valueColor.takeOrElse { MaterialTheme.colorScheme.onSurface },
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 14.sp, // design-ignore: autosize floor, no type-scale equivalent
                maxFontSize = MaterialTheme.typography.headlineSmall.fontSize,
                stepSize = 0.5.sp, // design-ignore: autosize step
            ),
        )
    }
}

@Immutable
data class StatCell(
    val label: String,
    val value: String,
    val unit: String? = null,
    val valueColor: Color = Color.Unspecified,
)

internal data class CellBorders(val end: Boolean, val bottom: Boolean)

/** Hairlines like mockup .grid3: right border except the last column/cell, bottom border except the last row. */
internal fun gridCellBorders(index: Int, count: Int, columns: Int): CellBorders {
    require(columns > 0) { "columns must be > 0" }
    require(index in 0 until count) { "index out of range" }
    val lastRowStart = ((count - 1) / columns) * columns
    return CellBorders(
        end = index % columns < columns - 1 && index < count - 1,
        bottom = index < lastRowStart,
    )
}

/** One card, [columns] equal cells per row, hairline separators (hr Detail 3×3 grid, spec §7.2). */
@Composable
fun StatGrid(cells: List<StatCell>, modifier: Modifier = Modifier, columns: Int = 3) {
    require(columns > 0) { "columns must be > 0" }
    val line = MaterialTheme.colorScheme.outline
    val shape = MaterialTheme.shapes.medium
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, line, shape),
    ) {
        cells.chunked(columns).forEachIndexed { row, rowCells ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                rowCells.forEachIndexed { column, cell ->
                    val borders = gridCellBorders(row * columns + column, cells.size, columns)
                    Metric(
                        label = cell.label,
                        value = cell.value,
                        unit = cell.unit,
                        valueColor = cell.valueColor,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .hairlines(borders, line)
                            .padding(vertical = AppTheme.spacing.md, horizontal = AppTheme.spacing.xs),
                    )
                }
                repeat(columns - rowCells.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private fun Modifier.hairlines(borders: CellBorders, color: Color): Modifier = drawBehind {
    val stroke = 1.dp.toPx()
    if (borders.end) {
        val x = if (layoutDirection == LayoutDirection.Ltr) size.width - stroke / 2 else stroke / 2
        drawLine(color, Offset(x, 0f), Offset(x, size.height), stroke)
    }
    if (borders.bottom) {
        val y = size.height - stroke / 2
        drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
    }
}
