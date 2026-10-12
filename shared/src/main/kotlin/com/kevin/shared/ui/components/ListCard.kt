package com.kevin.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kevin.shared.format.NO_VALUE
import com.kevin.shared.ui.theme.AppTheme

internal data class ListCardStyle(val container: Color, val border: Color)

internal fun listCardStyle(
    selected: Boolean,
    highlighted: Boolean,
    surface: Color,
    outline: Color,
    accent: Color,
    accentTint: Color,
): ListCardStyle = when {
    selected -> ListCardStyle(accentTint, accent)
    highlighted -> ListCardStyle(surface, accent)
    else -> ListCardStyle(surface, outline)
}

/** List row card (mockup .li): title, optional subtitle, optional trailing slot; selection and highlight states. */
@Composable
fun ListCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    selected: Boolean = false,
    highlighted: Boolean = false,
    selectionMode: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val style = listCardStyle(
        selected = selected,
        highlighted = highlighted,
        surface = MaterialTheme.colorScheme.surface,
        outline = MaterialTheme.colorScheme.outline,
        accent = MaterialTheme.colorScheme.primary,
        accentTint = AppTheme.colors.accentTint,
    )
    val shape = MaterialTheme.shapes.medium
    val click = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(onClick = onClick ?: {}, onLongClick = onLongClick)
    } else {
        Modifier
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(style.container)
            .border(1.dp, style.border, shape)
            .then(click)
            .padding(AppTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
    ) {
        if (selectionMode) Checkbox(checked = selected, onCheckedChange = null)
        leading?.invoke()
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing?.invoke()
    }
}

/** Trailing hero value for a ListCard (jump best height, hr duration): Light weight, small unit, "—" when absent. */
@Composable
fun ListCardValue(value: String?, modifier: Modifier = Modifier, unit: String? = null) {
    Text(
        text = metricValueText(value ?: NO_VALUE, unit, MaterialTheme.colorScheme.onSurfaceVariant),
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Light),
        color = if (value == null) AppTheme.colors.textDisabled else MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
    )
}
