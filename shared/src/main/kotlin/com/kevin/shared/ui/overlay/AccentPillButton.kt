package com.kevin.shared.ui.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppTheme

/**
 * Accent emphasis per DESIGN.md: 1dp accent border plus accentTint fill, pill shape, uppercase label.
 * Internal stand-in until sub-project 2's primary button is merged; replace it then.
 */
@Composable
internal fun AccentPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = AppTheme.pillShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppTheme.colors.accentTint,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Text(text.uppercase(), style = MaterialTheme.typography.labelLarge, maxLines = 1)
    }
}
