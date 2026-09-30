package com.kevin.shared.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (items.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        stringResource(
                            when (retention) {
                                TrashRetention.ON_NEXT_APP_START -> R.string.shared_trash_hint_next_app_start
                                TrashRetention.MANUAL -> R.string.shared_trash_hint_manual
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        if (items.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.shared_trash_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(items, key = { it.id }) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.label, style = MaterialTheme.typography.titleMedium)
                            Text(
                                entry.startedAt.toDateString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { onRestore(entry.id) }) {
                            Text(stringResource(R.string.shared_trash_restore))
                        }
                        if (onDeleteForever != null) {
                            IconButton(onClick = { pendingDeleteId = entry.id }) {
                                Icon(
                                    Icons.Default.DeleteForever,
                                    contentDescription = stringResource(R.string.shared_delete_forever_action),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.shared_soft_delete_title)) },
        text = { Text("$moved $retentionText") },
        confirmButton = {
            TextButton(onClick = { onConfirm(pendingIds) }) {
                Text(stringResource(R.string.shared_soft_delete_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.shared_trash_cancel)) }
        }
    )
}

@Composable
fun DeleteForeverConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.shared_delete_forever_title)) },
        text = { Text(message ?: stringResource(R.string.shared_delete_forever_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    stringResource(R.string.shared_delete_forever_action),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.shared_trash_cancel)) }
        }
    )
}
