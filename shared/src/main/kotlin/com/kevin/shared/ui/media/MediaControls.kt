package com.kevin.shared.ui.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.theme.AppTheme

/** Disabled controls on video (jump's resolution chip with no options, `RecordChrome.kt:342-390`). */
internal const val MediaDisabledAlpha = 0.4f

private val TrailingIconSize: Dp = 16.dp

/**
 * Icon control on video, e.g. camera switch or close: onMedia tint, 48dp target, no container.
 * [contentDescription] is the app's localized string.
 */
@Composable
fun MediaIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val onMedia = AppTheme.colors.onMedia
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = onMedia,
            disabledContentColor = onMedia.copy(alpha = MediaDisabledAlpha),
        ),
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

/**
 * Plain-text control on video, e.g. the resolution "1080p60 ▾": shadowed text plus an optional
 * trailing icon, no chip, no fill (§4.1). 48dp tall target. For a menu, wrap it in a Box with
 * the app's DropdownMenu.
 */
@Composable
fun MediaTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .heightIn(min = HudTouchTarget)
            .alpha(if (enabled) 1f else MediaDisabledAlpha)
            .clip(AppTheme.pillShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = AppTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MediaText(text = text, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        if (trailingIcon != null) {
            Icon(
                trailingIcon,
                contentDescription = null,
                tint = AppTheme.colors.onMedia,
                modifier = Modifier.size(TrailingIconSize),
            )
        }
    }
}
