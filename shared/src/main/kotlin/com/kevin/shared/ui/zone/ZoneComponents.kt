package com.kevin.shared.ui.zone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kevin.shared.R
import com.kevin.shared.ui.theme.AppTheme

@Composable
fun TargetZoneDialog(
    targetZone: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    zoneCount: Int = 5,
    zoneColors: List<Color> = AppTheme.colors.zones
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.shared_zone_dialog_title)) },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
                (1..zoneCount).forEach { z ->
                    val selected = targetZone == z
                    val zoneColor = zoneColors.getOrElse(z - 1) { MaterialTheme.colorScheme.outlineVariant }
                    FilterChip(
                        selected = selected,
                        onClick = { onSelect(z) },
                        label = { Text(stringResource(R.string.shared_zone_short, z)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = zoneColor,
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.shared_zone_dialog_close)) }
        }
    )
}
