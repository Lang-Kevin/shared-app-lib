package com.kevin.shared.ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.format.DurationStyle
import com.kevin.shared.format.formatElapsed
import com.kevin.shared.ui.components.ListCard
import com.kevin.shared.ui.components.ListCardValue
import com.kevin.shared.ui.components.Metric
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.components.SecondaryButton
import com.kevin.shared.ui.components.StatCell
import com.kevin.shared.ui.components.StatGrid
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.dialog.AppDialog
import com.kevin.shared.ui.theme.AppTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    if (categories.isEmpty()) return
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
    ) {
        categories.forEach { category ->
            val isSelected = selected.isEmpty() || category in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(category) },
                label = { Text(category) },
                shape = AppTheme.pillShape,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Transparent,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = AppTheme.colors.accentTint,
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderWidth = 1.dp,
                    selectedBorderWidth = 1.dp,
                ),
            )
        }
    }
}

/** Prefer [formatElapsed] with an explicit [DurationStyle] in new code. Kept for app call sites. */
fun durationString(seconds: Long): String = formatElapsed(seconds, DurationStyle.Compact)

fun Long.toDateString(): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(Date(this))

@Composable
fun SummaryCard(stats: List<Pair<String, String>>) {
    StatGrid(
        cells = stats.map { (label, value) -> StatCell(label = label, value = value) },
        columns = stats.size.coerceAtLeast(1),
    )
}

@Composable
fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Metric(label = label, value = value, modifier = modifier)
}

@Composable
fun SessionListItem(
    label: String,
    startedAt: Long,
    endedAt: Long?,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    ListCard(
        title = label,
        subtitle = startedAt.toDateString(),
        selected = isSelected,
        selectionMode = isSelectionMode,
        onClick = onClick,
        onLongClick = onLongClick,
        trailing = {
            if (endedAt != null) {
                ListCardValue(value = durationString((endedAt - startedAt) / 1000))
            } else {
                Text(
                    text = stringResource(R.string.shared_session_in_progress).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.recording,
                )
            }
        },
    )
}

@Composable
fun LeaveSessionDialog(
    onSave: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.shared_leave_session_title),
        text = stringResource(R.string.shared_leave_session_message),
        actions = {
            PrimaryButton(
                text = stringResource(R.string.shared_leave_session_save),
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton(
                text = stringResource(R.string.shared_leave_session_discard),
                onClick = onDiscard,
                modifier = Modifier.fillMaxWidth(),
                destructive = true,
            )
            TextActionButton(
                text = stringResource(R.string.shared_leave_session_keep_recording),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}
