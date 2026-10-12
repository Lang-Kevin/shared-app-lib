package com.kevin.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.domain.LabelItem
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.dialog.AppDialog
import com.kevin.shared.ui.theme.AppTheme

// ponytail: gemeinsamer Picker für ArmSwing (BarbellExercise) + TrackerApp (SportLabel)
@Composable
fun LabelPickerDialog(
    title: String,
    items: List<LabelItem>,
    initialSelection: String?,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = null,
    addFieldLabel: String? = null,
    onAdd: ((String) -> Unit)? = null,
    onDelete: ((LabelItem) -> Unit)? = null,
) {
    var selected by remember { mutableStateOf(initialSelection ?: items.firstOrNull()?.name ?: "") }
    var addText by remember { mutableStateOf("") }

    AppDialog(
        onDismissRequest = onDismiss,
        title = title,
        content = {
            LazyColumn(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs),
            ) {
                items(items, key = { it.id }) { item ->
                    val isSelected = selected == item.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = isSelected, role = Role.RadioButton) { selected = item.name }
                            .padding(vertical = AppTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    ) {
                        RadioButton(selected = isSelected, onClick = null)
                        Text(
                            text = item.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (onDelete != null && !item.isPredefined) {
                            IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.shared_label_picker_delete),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                if (onAdd != null && addFieldLabel != null) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.spacing.sm),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                        ) {
                            OutlinedTextField(
                                value = addText,
                                onValueChange = { addText = it },
                                label = { Text(addFieldLabel) },
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(
                                onClick = {
                                    onAdd(addText.trim())
                                    addText = ""
                                },
                                enabled = addText.trim().isNotEmpty(),
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = addFieldLabel)
                            }
                        }
                    }
                }
            }
        },
        actions = {
            PrimaryButton(
                text = confirmText,
                onClick = { onConfirm(selected) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selected.isNotEmpty(),
            )
            if (dismissText != null) {
                TextActionButton(text = dismissText, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
            }
        },
    )
}
