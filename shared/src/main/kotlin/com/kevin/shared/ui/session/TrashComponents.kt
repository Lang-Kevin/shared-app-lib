package com.kevin.shared.ui.session

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.ui.components.EmptyState
import com.kevin.shared.ui.components.ListCard
import com.kevin.shared.ui.components.SecondaryButton
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.dialog.AppDialog
import com.kevin.shared.ui.theme.AppTheme

/** How long soft-deleted items stay in the trash; UI texts must match the app's actual policy. */
enum class TrashRetention { ON_NEXT_APP_START, MANUAL }

data class TrashSessionItem(val id: Long, val label: String, val startedAt: Long)

@Composable
fun TrashTab(
    items: List<TrashSessionItem>,
    retention: TrashRetention,
    onRestore: (Long) -> Unit,
    modifier: Modifier = Modifier,
    onDeleteForever: ((Long) -> Unit)? = null,
    deleteForeverMessage: String? = null,
) {
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)) {
        if (items.isEmpty()) {
            item { EmptyState(title = stringResource(R.string.shared_trash_empty)) }
        } else {
            item {
                Text(
                    text = stringResource(
                        when (retention) {
                            TrashRetention.ON_NEXT_APP_START -> R.string.shared_trash_hint_next_app_start
                            TrashRetention.MANUAL -> R.string.shared_trash_hint_manual
                        }
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
                        .padding(AppTheme.spacing.md),
                )
            }
            items(items, key = { it.id }) { entry ->
                ListCard(
                    title = entry.label,
                    subtitle = entry.startedAt.toDateString(),
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextActionButton(
                                text = stringResource(R.string.shared_trash_restore),
                                onClick = { onRestore(entry.id) },
                            )
                            if (onDeleteForever != null) {
                                IconButton(onClick = { pendingDeleteId = entry.id }) {
                                    Icon(
                                        Icons.Default.DeleteForever,
                                        contentDescription = stringResource(R.string.shared_delete_forever_action),
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    },
                )
            }
        }
        item { Spacer(Modifier.height(AppTheme.spacing.lg)) }
    }

    val pendingId = pendingDeleteId
    if (pendingId != null && onDeleteForever != null) {
        DeleteForeverConfirmationDialog(
            onConfirm = {
                pendingDeleteId = null
                onDeleteForever(pendingId)
            },
            onDismiss = { pendingDeleteId = null },
            message = deleteForeverMessage
        )
    }
}

@Composable
fun SoftDeleteConfirmationDialog(
    pendingIds: List<Long>?,
    retention: TrashRetention,
    onConfirm: (List<Long>) -> Unit,
    onDismiss: () -> Unit
) {
    pendingIds ?: return
    val count = pendingIds.size
    val moved = pluralStringResource(R.plurals.shared_soft_delete_message, count, count)
    val retentionText = stringResource(
        when (retention) {
            TrashRetention.ON_NEXT_APP_START -> R.string.shared_soft_delete_retention_next_app_start
            TrashRetention.MANUAL -> R.string.shared_soft_delete_retention_manual
        }
    )
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.shared_soft_delete_title),
        text = "$moved $retentionText",
        actions = {
            SecondaryButton(
                text = stringResource(R.string.shared_soft_delete_confirm),
                onClick = { onConfirm(pendingIds) },
                modifier = Modifier.fillMaxWidth(),
                destructive = true,
            )
            TextActionButton(
                text = stringResource(R.string.shared_trash_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}

@Composable
fun DeleteForeverConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String? = null,
) {
    AppDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.shared_delete_forever_title),
        text = message ?: stringResource(R.string.shared_delete_forever_message),
        actions = {
            SecondaryButton(
                text = stringResource(R.string.shared_delete_forever_action),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                destructive = true,
            )
            TextActionButton(
                text = stringResource(R.string.shared_trash_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}
