package com.kevin.shared.ui.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.format.formatPercent
import com.kevin.shared.ui.components.TextActionButton

/** [done] of [total] as 0..1; total <= 0 -> 0. */
fun progressFraction(done: Int, total: Int): Float =
    if (total <= 0) 0f else (done.toFloat() / total).coerceIn(0f, 1f)

internal fun clampProgress(progress: Float): Float = if (progress.isNaN()) 0f else progress.coerceIn(0f, 1f)

/** Blocking progress (analysis, export). [progress] null = indeterminate. Cancellable only with [onCancel]. */
@Composable
fun ProgressDialog(
    title: String,
    progress: Float?,
    modifier: Modifier = Modifier,
    message: String? = null,
    onCancel: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.shared_action_cancel),
) {
    val locale = LocalConfiguration.current.locales[0]
    val accent = MaterialTheme.colorScheme.primary
    val track = MaterialTheme.colorScheme.outline
    AppDialog(
        onDismissRequest = { onCancel?.invoke() },
        title = title,
        modifier = modifier,
        text = message,
        dismissOnBackPress = onCancel != null,
        dismissOnClickOutside = false,
        content = {
            if (progress != null) {
                val p = clampProgress(progress)
                LinearProgressIndicator(
                    progress = { p },
                    modifier = Modifier.fillMaxWidth(),
                    color = accent,
                    trackColor = track,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )
                Text(
                    text = formatPercent(p, locale),
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = accent,
                    trackColor = track,
                    gapSize = 0.dp,
                )
            }
        },
        actions = {
            if (onCancel != null) {
                TextActionButton(text = cancelText, onClick = onCancel, modifier = Modifier.fillMaxWidth())
            }
        },
    )
}
