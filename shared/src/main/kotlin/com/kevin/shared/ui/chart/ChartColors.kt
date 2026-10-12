package com.kevin.shared.ui.chart

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.kevin.shared.ui.theme.AppColors
import com.kevin.shared.ui.theme.AppTheme

/** Resolved chart color roles. Build with [ChartDefaults.colors]; never from raw colors. */
@Immutable
data class ChartColors(
    val line: Color,
    val secondaryLine: Color,
    val axisLabel: Color,
    val zoneLabel: Color,
    val targetZoneLabel: Color,
    val targetBand: Color,
    val neutralBand: Color,
    val bandLabel: Color,
    val goalFill: Color,
    val goalBorder: Color,
    val goalText: Color,
    val accent: Color,
    val marker: Color,
    val markerLine: Color,
    val scrubLine: Color,
    val tooltipFill: Color,
    val tooltipBorder: Color,
    val tooltipText: Color,
    val bar: Color,
    val barHighlight: Color,
    val barHighlightBorder: Color,
    val barLabel: Color,
    val barLabelHighlight: Color,
    val sectionLabel: Color,
    val legend: Color,
    val legendTarget: Color,
    val zones: List<Color>,
)

/** Token → chart role mapping (mockup `00-final-screens.html` 106-229). Pure, JVM-tested. */
internal fun chartColors(scheme: ColorScheme, app: AppColors): ChartColors = ChartColors(
    line = scheme.onSurface,
    secondaryLine = scheme.onSurface.copy(alpha = 0.45f),
    axisLabel = app.textDisabled,
    zoneLabel = scheme.onSurfaceVariant,
    targetZoneLabel = scheme.onSurface,
    targetBand = scheme.primary.copy(alpha = 0.08f),
    neutralBand = scheme.onSurface.copy(alpha = 0.04f),
    bandLabel = app.textDisabled,
    goalFill = app.accentTint,
    goalBorder = scheme.primary,
    goalText = scheme.onSurface,
    accent = scheme.primary,
    marker = app.highlight,
    markerLine = app.highlight.copy(alpha = 0.7f),
    scrubLine = scheme.onSurface.copy(alpha = 0.5f),
    tooltipFill = app.tooltipContainer,
    tooltipBorder = scheme.outlineVariant,
    tooltipText = scheme.onSurface,
    bar = scheme.outlineVariant,
    barHighlight = app.accentTint,
    barHighlightBorder = scheme.primary,
    barLabel = app.textDisabled,
    barLabelHighlight = scheme.onSurface,
    sectionLabel = scheme.onSurfaceVariant,
    legend = app.textDisabled,
    legendTarget = scheme.onSurface,
    zones = app.zones,
)

/** Ramp color of 1-based zone [number], clamped; [ChartColors.line] if the ramp is empty. */
internal fun ChartColors.zoneColor(number: Int): Color =
    if (zones.isEmpty()) line else zones[(number - 1).coerceIn(0, zones.lastIndex)]

/** Chart dimensions and theme-resolved defaults. */
object ChartDefaults {
    val LineWidth: Dp = 1.5.dp
    val ThinLineWidth: Dp = 1.dp
    val DotRadius: Dp = 3.5.dp
    val TrendDotRadius: Dp = 3.dp
    val DashOn: Dp = 4.dp
    val DashOff: Dp = 3.dp
    val MarkerDash: Dp = 3.dp
    val LabelInset: Dp = 4.dp
    val LabelGap: Dp = 2.dp
    val PillPaddingH: Dp = 6.dp
    val PillPaddingV: Dp = 2.dp
    /** Room above the plot for tooltip, marker and last-point labels. */
    val TopInset: Dp = 20.dp
    val AxisHeight: Dp = 14.dp
    val BarGap: Dp = 8.dp
    val BarCorner: Dp = 3.dp
    val ZoneBarHeight: Dp = 4.dp
    val ZoneBarGap: Dp = 2.dp
    val MiniZoneBarWidth: Dp = 58.dp
    val MiniZoneBarHeight: Dp = 3.dp
    val MiniZoneBarGap: Dp = 1.dp
    const val MaxPoints: Int = 600
    const val MaxTimeTicks: Int = 5

    @Composable
    @ReadOnlyComposable
    fun colors(): ChartColors = chartColors(MaterialTheme.colorScheme, AppTheme.colors)

    /** labelSmall (tracked caps), e.g. the GOAL badge. */
    @Composable
    @ReadOnlyComposable
    fun labelStyle(): TextStyle = MaterialTheme.typography.labelSmall

    /** labelSmall without tracking, for numbers (ticks, values, tooltip). */
    @Composable
    @ReadOnlyComposable
    fun numberStyle(): TextStyle = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.em)
}
