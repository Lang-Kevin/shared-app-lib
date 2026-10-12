package com.kevin.shared.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.kevin.shared.ui.theme.AppTheme

/** Night Drive tab row (mockup .tabs): hairline bottom, uppercase labels, 2dp inset accent underline. */
@Composable
fun AppTabRow(titles: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val tokens = segmentTokens()
    val hairline = MaterialTheme.colorScheme.outline
    val inset = AppTheme.spacing.md
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = 1.dp.toPx()
                val y = size.height - stroke / 2
                drawLine(hairline, Offset(0f, y), Offset(size.width, y), stroke)
            }
            .selectableGroup(),
    ) {
        titles.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .selectable(selected = selected, role = Role.Tab) { onSelect(index) }
                    .drawBehind {
                        if (selected) {
                            val height = 2.dp.toPx()
                            val x = inset.toPx().coerceAtMost(size.width / 4)
                            drawRect(tokens.accent, Offset(x, size.height - height), Size(size.width - 2 * x, height))
                        }
                    }
                    .padding(vertical = AppTheme.spacing.md, horizontal = AppTheme.spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.14.em),
                    color = tabContentColor(selected, tokens),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
