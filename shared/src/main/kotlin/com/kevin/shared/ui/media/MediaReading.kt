package com.kevin.shared.ui.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kevin.shared.ui.theme.AppTheme

/** Shown when a reading has no value yet, e.g. no Best before the first jump. En dash. */
const val MediaReadingPlaceholder = "–"

internal const val MediaLabelAlpha = 0.85f
internal const val MediaUnitAlpha = 0.80f

internal data class ReadingText(val value: String, val unit: String?)

/** A missing or blank value shows [placeholder] and drops the unit ("– cm" reads like a value). */
internal fun readingText(value: String?, unit: String?, placeholder: String = MediaReadingPlaceholder): ReadingText =
    if (value.isNullOrBlank()) ReadingText(placeholder, null)
    else ReadingText(value, unit?.takeIf { it.isNotBlank() })

/**
 * A labelled number on video, e.g. Last / Best beside the shutter (spec §7.3 A2). No box.
 * [accentLabel] colours the label in the accent with the emphasized shadow (Best).
 * The value is one line and shrinks via autoSize instead of wrapping.
 * [label], [value] and [unit] come from the app's string resources and formatters.
 */
@Composable
fun MediaReading(
    label: String,
    value: String?,
    modifier: Modifier = Modifier,
    unit: String? = null,
    accentLabel: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val reading = readingText(value, unit)
    val onMedia = AppTheme.colors.onMedia
    val typography = MaterialTheme.typography
    Column(modifier = modifier, horizontalAlignment = horizontalAlignment) {
        MediaText(
            text = label.uppercase(),
            style = typography.labelSmall,
            color = if (accentLabel) MaterialTheme.colorScheme.primary else onMedia.copy(alpha = MediaLabelAlpha),
            emphasized = accentLabel,
            maxLines = 1,
        )
        Row(horizontalArrangement = Arrangement.aligned(horizontalAlignment)) {
            MediaText(
                text = reading.value,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .alignByBaseline(),
                style = typography.headlineLarge.copy(fontWeight = FontWeight.Light),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = typography.titleMedium.fontSize,
                    maxFontSize = typography.headlineLarge.fontSize,
                ),
            )
            val unitText = reading.unit
            if (unitText != null) {
                Spacer(Modifier.width(AppTheme.spacing.xs))
                MediaText(
                    text = unitText,
                    modifier = Modifier.alignByBaseline(),
                    style = typography.bodySmall,
                    color = onMedia.copy(alpha = MediaUnitAlpha),
                    maxLines = 1,
                )
            }
        }
    }
}
