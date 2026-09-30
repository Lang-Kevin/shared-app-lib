package com.kevin.shared.ui.selection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.kevin.shared.R

/**
 * Header for list selection mode: [close] "n selected" [delete].
 * Registers a BackHandler that calls onClear. Applies no window insets itself.
 */
@Composable
fun SelectionHeader(
    count: Int,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onClear)
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClear) {
            Icon(
                Icons.Default.Close,
                contentDescription = stringResource(R.string.shared_selection_cancel)
            )
        }
        Text(
            text = pluralStringResource(R.plurals.shared_selection_count, count, count),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete, enabled = count > 0) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.shared_selection_delete),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
