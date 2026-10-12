package com.kevin.shared.ui.zone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.ui.components.SegmentedControl
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.dialog.AppDialog
import com.kevin.shared.ui.theme.AppTheme

internal fun zoneStripAlpha(zone: Int, targetZone: Int): Float = if (zone == targetZone) 1f else 0.35f

@Composable
fun TargetZoneDialog(
    targetZone: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    zoneCount: Int = 5,
    zoneColors: List<Color> = AppTheme.colors.zones
) {
    val fallback = MaterialTheme.colorScheme.outlineVariant
    val labels = (1..zoneCount).map { zone -> stringResource(R.string.shared_zone_short, zone) }
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.shared_zone_dialog_title),
        content = {
            SegmentedControl(
                options = labels,
                selectedIndex = targetZone - 1,
                onSelect = { index -> onSelect(index + 1) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            ) {
                (1..zoneCount).forEach { zone ->
                    val color = zoneColors.getOrElse(zone - 1) { fallback }
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .clip(AppTheme.pillShape)
                            .background(color.copy(alpha = zoneStripAlpha(zone, targetZone))),
                    )
                }
            }
        },
        actions = {
            TextActionButton(
                text = stringResource(R.string.shared_zone_dialog_close),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}
