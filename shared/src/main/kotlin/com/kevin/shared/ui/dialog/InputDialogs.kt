package com.kevin.shared.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import com.kevin.shared.R
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.theme.AppTheme

internal fun textInputConfirmEnabled(text: String, allowEmpty: Boolean): Boolean = allowEmpty || text.isNotBlank()

internal fun textInputResult(text: String): String = text.trim()

internal fun <T> singleChoiceConfirmEnabled(selected: T?, options: List<T>): Boolean =
    selected != null && selected in options

/** One text field (rename, label, note). Confirms the trimmed text; blank is rejected unless [allowEmpty]. */
@Composable
fun TextInputDialog(
    title: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    allowEmpty: Boolean = false,
    confirmText: String = stringResource(R.string.shared_action_save),
    dismissText: String = stringResource(R.string.shared_action_cancel),
) {
    var text by rememberSaveable { mutableStateOf(initialValue) }
    val canConfirm = textInputConfirmEnabled(text, allowEmpty)
    val confirm = { if (canConfirm) onConfirm(textInputResult(text)) }
    AppDialog(
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier,
        content = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = if (label != null) { { Text(label) } } else null,
                placeholder = if (placeholder != null) { { Text(placeholder) } } else null,
                singleLine = singleLine,
                shape = MaterialTheme.shapes.small,
                keyboardOptions = KeyboardOptions(imeAction = if (singleLine) ImeAction.Done else ImeAction.Default),
                keyboardActions = KeyboardActions(onDone = { confirm() }),
            )
        },
        actions = {
            PrimaryButton(text = confirmText, onClick = confirm, modifier = Modifier.fillMaxWidth(), enabled = canConfirm)
            TextActionButton(text = dismissText, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        },
    )
}

/** Pick one of [options] with radio rows, then confirm. [selected] pre-selects; null starts with nothing selected. */
@Composable
fun <T> SingleChoiceDialog(
    title: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> String,
    onConfirm: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = stringResource(R.string.shared_action_confirm),
    dismissText: String = stringResource(R.string.shared_action_cancel),
) {
    var choice by remember(options, selected) { mutableStateOf(selected) }
    AppDialog(
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier,
        content = {
            Column(Modifier.selectableGroup().verticalScroll(rememberScrollState())) {
                options.forEach { option ->
                    val isSelected = option == choice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = isSelected, role = Role.RadioButton) { choice = option }
                            .padding(vertical = AppTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                    ) {
                        RadioButton(selected = isSelected, onClick = null)
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        },
        actions = {
            val current = choice
            PrimaryButton(
                text = confirmText,
                onClick = { if (current != null && singleChoiceConfirmEnabled(current, options)) onConfirm(current) },
                modifier = Modifier.fillMaxWidth(),
                enabled = singleChoiceConfirmEnabled(current, options),
            )
            TextActionButton(text = dismissText, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        },
    )
}
