package com.kevin.shared.ui.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kevin.shared.R
import com.kevin.shared.ui.theme.AppTheme

/**
 * Stacked time-in-zone bar: one segment per zone with duration > 0 in its ramp color, [gap]
 * between drawn segments, no radius. [durations] index 0 = Z1. Size it via [modifier]:
 * section `fillMaxWidth().height(ChartDefaults.ZoneBarHeight)`; History mini bar
 * `size(ChartDefaults.MiniZoneBarWidth, ChartDefaults.MiniZoneBarHeight)` with
 * `gap = ChartDefaults.MiniZoneBarGap`. All-zero durations draw nothing.
 */
@Composable
fun ZoneDistributionBar(
    durations: List<Long>,
    modifier: Modifier = Modifier,
    gap: Dp = ChartDefaults.ZoneBarGap,
    contentDescription: String? = null,
    colors: ChartColors = ChartDefaults.colors(),
) {
    val a11y = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Canvas(modifier.then(a11y)) {
        for (s in zoneSegments(durations, size.width, gap.toPx())) {
            drawRect(colors.zoneColor(s.index + 1), Offset(s.left, 0f), Size(s.width, size.height))
        }
    }
}

/**
 * Time-in-zone section (mockup 118-120): header row [title] (left) + [targetSummary] (right,
 * e.g. "62% IN TARGET ZONE"), the zone bar, and a legend "Z1 4:12 … Z5 0:40" where [targetZone]
 * is emphasized. [durations] index 0 = Z1; the unit is the caller's, [durationText] formats it.
 */
@Composable
fun TimeInZoneSection(
    durations: List<Long>,
    durationText: (Long) -> String,
    modifier: Modifier = Modifier,
    title: String? = null,
    targetZone: Int? = null,
    targetSummary: String? = null,
    contentDescription: String? = null,
    colors: ChartColors = ChartDefaults.colors(),
) {
    val a11y = if (contentDescription != null) {
        Modifier.semantics(mergeDescendants = true) { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Column(modifier.then(a11y), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        if (title != null || targetSummary != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title.orEmpty(), style = MaterialTheme.typography.labelSmall, color = colors.sectionLabel)
                if (targetSummary != null) {
                    Text(targetSummary, style = MaterialTheme.typography.labelSmall, color = colors.legendTarget)
                }
            }
        }
        ZoneDistributionBar(
            durations = durations,
            modifier = Modifier.fillMaxWidth().height(ChartDefaults.ZoneBarHeight),
            colors = colors,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            durations.forEachIndexed { i, d ->
                val zone = stringResource(R.string.shared_zone_short, i + 1)
                val item = "$zone ${durationText(d)}"
                Text(
                    item,
                    style = ChartDefaults.numberStyle(),
                    color = if (i + 1 == targetZone) colors.legendTarget else colors.legend,
                )
            }
        }
    }
}
