package com.kevin.shared.ui.zone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TargetZoneDialog(
    targetZone: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    zoneCount: Int = 5,
    zoneColors: List<Color> = emptyList()
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ziel-Zone wählen") },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..zoneCount).forEach { z ->
                    val selected = targetZone == z
                    val zoneColor = zoneColors.getOrElse(z - 1) { Color.Gray }
                    FilterChip(
                        selected = selected,
                        onClick = { onSelect(z) },
                        label = { Text("Z$z") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = zoneColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Schließen") }
        }
    )
}
