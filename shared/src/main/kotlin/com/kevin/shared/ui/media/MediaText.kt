package com.kevin.shared.ui.media

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kevin.shared.ui.theme.AppTheme

/** Shadow alpha for emphasized over-video text, e.g. the accent Best label (mockup 02: 0.9 instead of 0.75). */
internal const val EmphasizedShadowAlpha = 0.9f

internal fun mediaTextStyle(base: TextStyle, color: Color, shadow: Shadow): TextStyle =
    base.copy(color = color, shadow = shadow)

internal fun emphasizedShadow(shadow: Shadow): Shadow =
    shadow.copy(color = shadow.color.copy(alpha = EmphasizedShadowAlpha))

/**
 * Text drawn directly on camera or video (spec §4.1 "Over video"): the token text shadow,
 * no box, no pill. Use [emphasized] for accent-coloured text, which needs the stronger shadow.
 * Single shadow only, no halo.
 */
@Composable
fun MediaText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = AppTheme.colors.onMedia,
    emphasized: Boolean = false,
    textAlign: TextAlign = TextAlign.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    autoSize: TextAutoSize? = null,
) {
    val tokenShadow = AppTheme.colors.mediaTextShadow
    val shadow = if (emphasized) emphasizedShadow(tokenShadow) else tokenShadow
    Text(
        text = text,
        modifier = modifier,
        style = mediaTextStyle(style, color, shadow),
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = overflow,
        autoSize = autoSize,
    )
}
