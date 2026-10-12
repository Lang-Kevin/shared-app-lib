package com.kevin.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.kevin.shared.ui.theme.AppTheme

internal data class SegmentTokens(
    val accent: Color,
    val accentTint: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val textDisabled: Color,
    val outlineVariant: Color,
)

@Composable
@ReadOnlyComposable
internal fun segmentTokens(): SegmentTokens = SegmentTokens(
    accent = MaterialTheme.colorScheme.primary,
    accentTint = AppTheme.colors.accentTint,
    onSurface = MaterialTheme.colorScheme.onSurface,
    onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant,
    textDisabled = AppTheme.colors.textDisabled,
    outlineVariant = MaterialTheme.colorScheme.outlineVariant,
)

internal data class SegmentStyle(val container: Color, val content: Color, val border: Color)

internal fun segmentStyle(selected: Boolean, enabled: Boolean, tokens: SegmentTokens): SegmentStyle = when {
    !enabled && selected -> SegmentStyle(Color.Transparent, tokens.onSurfaceVariant, tokens.outlineVariant)
    !enabled -> SegmentStyle(Color.Transparent, tokens.textDisabled, Color.Transparent)
    selected -> SegmentStyle(tokens.accentTint, tokens.onSurface, tokens.accent)
    else -> SegmentStyle(Color.Transparent, tokens.onSurfaceVariant, Color.Transparent)
}

/** Divider [dividerIndex] sits between segment dividerIndex and dividerIndex + 1; hidden next to the selection. */
internal fun segmentDividerVisible(dividerIndex: Int, selectedIndex: Int): Boolean =
    dividerIndex != selectedIndex && dividerIndex + 1 != selectedIndex

internal fun tabContentColor(selected: Boolean, tokens: SegmentTokens): Color =
    if (selected) tokens.onSurface else tokens.textDisabled

/** Single-choice pill control (mockup .seg). Equal-width segments, no icon slot, labels wrap to 2 lines. */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val tokens = segmentTokens()
    val shape = AppTheme.pillShape
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .border(1.dp, tokens.outlineVariant, shape)
            .selectableGroup(),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val style = segmentStyle(selected, enabled, tokens)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(style.container)
                    .border(1.dp, style.border)
                    .selectable(selected = selected, enabled = enabled, role = Role.RadioButton) { onSelect(index) }
                    .padding(vertical = AppTheme.spacing.sm, horizontal = AppTheme.spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.1.em),
                    color = style.content,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (index < options.lastIndex) {
                Box(
                    Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(if (segmentDividerVisible(index, selectedIndex)) tokens.outlineVariant else Color.Transparent),
                )
            }
        }
    }
}
