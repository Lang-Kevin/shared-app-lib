package com.kevin.shared.ui.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kevin.shared.R
import com.kevin.shared.ui.components.SecondaryButton
import com.kevin.shared.ui.theme.AppTheme

/** Numeric text field (mockup .field). The caller owns the text and decides [errorText] (see [parseNumberInput]). */
@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
    unit: String? = null,
    errorText: String? = null,
    supportingText: String? = null,
    onDone: (() -> Unit)? = null,
    showDoneButton: Boolean = false,
) {
    val support = errorText ?: supportingText
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            label = { Text(label) },
            suffix = if (unit != null) { { Text(unit) } } else null,
            supportingText = if (support != null) { { Text(support) } } else null,
            isError = errorText != null,
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = if (onDone != null) KeyboardActions(onDone = { onDone() }) else KeyboardActions.Default,
        )
        if (showDoneButton && onDone != null) {
            SecondaryButton(text = stringResource(R.string.shared_action_ok), onClick = onDone)
        }
    }
}
