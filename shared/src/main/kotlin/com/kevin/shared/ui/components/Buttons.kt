package com.kevin.shared.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.kevin.shared.ui.theme.AppTheme

internal enum class ButtonKind { Primary, Secondary, Destructive }

internal data class ButtonPalette(val container: Color, val content: Color, val border: Color)

internal data class ButtonTokens(
    val accent: Color,
    val accentTint: Color,
    val onSurface: Color,
    val outlineVariant: Color,
    val error: Color,
    val textDisabled: Color,
)

/** Night Drive: accent only on Primary; destructive is error, never accent; disabled is colourless. */
internal fun buttonPalette(kind: ButtonKind, enabled: Boolean, tokens: ButtonTokens): ButtonPalette = when {
    !enabled -> ButtonPalette(Color.Transparent, tokens.textDisabled, tokens.outlineVariant)
    kind == ButtonKind.Primary -> ButtonPalette(tokens.accentTint, tokens.onSurface, tokens.accent)
    kind == ButtonKind.Secondary -> ButtonPalette(Color.Transparent, tokens.onSurface, tokens.outlineVariant)
    else -> ButtonPalette(Color.Transparent, tokens.error, tokens.error.copy(alpha = 0.45f))
}

@Composable
@ReadOnlyComposable
internal fun buttonTokens(): ButtonTokens = ButtonTokens(
    accent = MaterialTheme.colorScheme.primary,
    accentTint = AppTheme.colors.accentTint,
    onSurface = MaterialTheme.colorScheme.onSurface,
    outlineVariant = MaterialTheme.colorScheme.outlineVariant,
    error = MaterialTheme.colorScheme.error,
    textDisabled = AppTheme.colors.textDisabled,
)

/** Tracking for uppercase action/segment/tab labels (mockup .btnP: .16em). */
internal val LabelTracking: TextUnit = 0.16.em

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
    PillButton(text, onClick, modifier, enabled, ButtonKind.Primary)

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
) = PillButton(text, onClick, modifier, enabled, if (destructive) ButtonKind.Destructive else ButtonKind.Secondary)

/** Dismiss-style action (mockup .btnT): no border, uppercase, onSurfaceVariant. */
@Composable
fun TextActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = AppTheme.pillShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = AppTheme.colors.textDisabled,
        ),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.12.em),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PillButton(text: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean, kind: ButtonKind) {
    val palette = buttonPalette(kind, enabled, buttonTokens())
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = AppTheme.pillShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = palette.container,
            contentColor = palette.content,
            disabledContainerColor = palette.container,
            disabledContentColor = palette.content,
        ),
        border = BorderStroke(1.dp, palette.border),
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.lg, vertical = AppTheme.spacing.md),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = LabelTracking),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
