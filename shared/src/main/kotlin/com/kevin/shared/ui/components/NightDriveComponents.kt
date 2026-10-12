package com.kevin.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kevin.shared.format.NO_VALUE
import com.kevin.shared.ui.theme.AppTheme

/** Uppercase label over a large value with a small trailing unit; null [value] renders the muted dash. */
@Composable
fun HeroValue(
    label: String,
    value: String?,
    unit: String?,
    style: TextStyle,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(modifier, horizontalAlignment = horizontalAlignment) {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (value == null) {
            Text(NO_VALUE, style = style, color = AppTheme.colors.textDisabled)
        } else {
            Row {
                Text(value, style = style, modifier = Modifier.alignByBaseline())
                if (unit != null) {
                    Text(
                        unit,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alignByBaseline().padding(start = AppTheme.spacing.xs),
                    )
                }
            }
        }
    }
}

/** Accent-bordered pill (accent rarity: one per screen). Not uppercased, so units keep their case. */
@Composable
fun AccentTag(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.primary, AppTheme.pillShape)
            .background(AppTheme.colors.accentTint, AppTheme.pillShape)
            .padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.xs),
    )
}

/** 1 dp hairline fading transparent, accentSoft, transparent. */
@Composable
fun AccentDivider(modifier: Modifier = Modifier) {
    val soft = AppTheme.colors.accentSoft
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Brush.horizontalGradient(listOf(Color.Transparent, soft, Color.Transparent))),
    )
}

/** 3 dp rounded bar for table rows; [fraction] (clamped to 0..1) of the available width. */
@Composable
fun InlineBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth(inlineBarFraction(fraction))
            .height(3.dp)
            .background(color, AppTheme.pillShape),
    )
}

internal fun inlineBarFraction(fraction: Float): Float = fraction.coerceIn(0f, 1f)
