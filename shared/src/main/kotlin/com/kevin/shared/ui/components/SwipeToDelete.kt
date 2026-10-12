package com.kevin.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kevin.shared.R
import com.kevin.shared.ui.theme.AppTheme
import kotlinx.coroutines.launch

/** Fraction of the row width the card must travel before a release deletes. */
internal const val SwipeDeleteFraction = 0.4f

/** Alpha of the error tint behind a swiped row. */
internal const val SwipeTintAlpha = 0.18f

internal fun swipeDeleteThreshold(totalDistance: Float): Float = totalDistance * SwipeDeleteFraction

internal fun shouldRequestDelete(value: SwipeToDismissBoxValue, enabled: Boolean): Boolean =
    enabled && value == SwipeToDismissBoxValue.EndToStart

internal fun showDeleteBackground(direction: SwipeToDismissBoxValue): Boolean =
    direction == SwipeToDismissBoxValue.EndToStart

internal fun swipeDeleteContainer(error: Color): Color = error.copy(alpha = SwipeTintAlpha)

/**
 * Swipe a row towards the start to delete it. [onDelete] fires once per completed swipe; the row then
 * animates back, so a confirm dialog or undo snackbar finds it in place.
 */
@Composable
fun SwipeToDelete(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val state = rememberSwipeToDismissBoxState(positionalThreshold = ::swipeDeleteThreshold)
    val currentOnDelete by rememberUpdatedState(onDelete)
    val currentEnabled by rememberUpdatedState(enabled)
    val scope = rememberCoroutineScope()
    // Stable identity: SwipeToDismissBox keys its LaunchedEffect on onDismiss (Review Focus 2).
    val handleDismiss: (SwipeToDismissBoxValue) -> Unit = remember(state, scope) {
        { value ->
            scope.launch { state.reset() }
            if (shouldRequestDelete(value, currentEnabled)) currentOnDelete()
        }
    }
    val error = MaterialTheme.colorScheme.error
    val shape = MaterialTheme.shapes.medium
    SwipeToDismissBox(
        state = state,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        gesturesEnabled = enabled,
        onDismiss = handleDismiss,
        backgroundContent = {
            if (showDeleteBackground(state.dismissDirection)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(swipeDeleteContainer(error))
                        .padding(horizontal = AppTheme.spacing.lg),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.shared_action_delete),
                        tint = error,
                    )
                }
            }
        },
        content = content,
    )
}
