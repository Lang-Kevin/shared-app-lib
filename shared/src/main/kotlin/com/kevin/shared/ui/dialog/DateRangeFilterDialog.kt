package com.kevin.shared.ui.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.format.pickerMillisToLocalDate
import com.kevin.shared.format.toPickerMillis
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.theme.AppTheme
import java.time.LocalDate

/** Both picker ends (UTC-midnight millis) as LocalDates, or null while the range is incomplete. */
internal fun dateRangeSelection(startMillis: Long?, endMillis: Long?): Pair<LocalDate, LocalDate>? =
    if (startMillis == null || endMillis == null) {
        null
    } else {
        pickerMillisToLocalDate(startMillis) to pickerMillisToLocalDate(endMillis)
    }

/** Inclusive date-range filter. Emits LocalDates; callers convert to their own time zone (Review Focus 3). */
@Composable
fun DateRangeFilterDialog(
    onConfirm: (start: LocalDate, end: LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialStart: LocalDate? = null,
    initialEnd: LocalDate? = null,
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStart?.toPickerMillis(),
        initialSelectedEndDateMillis = initialEnd?.toPickerMillis(),
    )
    val range = dateRangeSelection(state.selectedStartDateMillis, state.selectedEndDateMillis)
    val colors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        dayInSelectionRangeContainerColor = AppTheme.colors.accentTint,
    )
    val dateFormatter = remember { DatePickerDefaults.dateFormatter() }
    val inset = PaddingValues(start = AppTheme.spacing.xl, end = AppTheme.spacing.md)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            PrimaryButton(
                text = stringResource(R.string.shared_action_apply),
                onClick = { range?.let { (start, end) -> onConfirm(start, end) } },
                enabled = range != null,
            )
        },
        modifier = modifier,
        dismissButton = {
            TextActionButton(text = stringResource(R.string.shared_action_cancel), onClick = onDismiss)
        },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 0.dp,
        colors = colors,
    ) {
        DialogScrim()
        DateRangePicker(
            state = state,
            modifier = Modifier.weight(1f),
            dateFormatter = dateFormatter,
            colors = colors,
            title = {
                Text(
                    text = stringResource(R.string.shared_date_range_title).uppercase(),
                    modifier = Modifier.padding(inset).padding(top = AppTheme.spacing.lg),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = state.selectedStartDateMillis,
                    selectedEndDateMillis = state.selectedEndDateMillis,
                    displayMode = state.displayMode,
                    dateFormatter = dateFormatter,
                    modifier = Modifier.padding(inset).padding(bottom = AppTheme.spacing.md),
                )
            },
            showModeToggle = false,
        )
    }
}
