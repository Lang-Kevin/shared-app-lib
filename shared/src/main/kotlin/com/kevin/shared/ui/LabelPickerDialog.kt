package com.kevin.shared.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevin.shared.domain.LabelItem

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = item.name }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == item.name,
                            onClick = { selected = item.name },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(item.name, modifier = Modifier.weight(1f))
                        if (onDelete != null && !item.isPredefined) {
                            IconButton(
                                onClick = { onDelete(item) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Löschen"
                                )
                            }
                        }
                    }
                }
                if (onAdd != null && addFieldLabel != null) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = addText,
                                onValueChange = { addText = it },
                                label = { Text(addFieldLabel) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            IconButton(
                                onClick = {
                                    onAdd(addText.trim())
                                    addText = ""
                                },
                                enabled = addText.trim().isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = addFieldLabel
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selected) },
                enabled = selected.isNotEmpty()
            ) {
                Text(confirmText)
            }
        },
        dismissButton = if (dismissText != null) {
            {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        } else {
            null
        }
    )
}
