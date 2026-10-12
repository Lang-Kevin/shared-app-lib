package com.kevin.shared.ui.overlay

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kevin.shared.ui.components.PrimaryButton
import com.kevin.shared.ui.theme.AppTheme

enum class PermissionGateMode { Granted, Request, OpenSettings }

/**
 * Which action the gate offers.
 *
 * `shouldShowRationale` is false both before the first request and after a permanent denial,
 * so [requestedOnce] tells them apart. Keep it in `rememberSaveable` and set it to true in the
 * permission result callback. After an app restart it is false again: the first tap re-requests,
 * the system denies at once, the callback sets it, and the gate switches to OpenSettings.
 */
fun permissionGateMode(granted: Boolean, shouldShowRationale: Boolean, requestedOnce: Boolean): PermissionGateMode =
    when {
        granted -> PermissionGateMode.Granted
        shouldShowRationale -> PermissionGateMode.Request
        requestedOnce -> PermissionGateMode.OpenSettings
        else -> PermissionGateMode.Request
    }

/** Opens this app's system settings page, for permissions the user denied permanently. */
fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private val PermissionIconSize: Dp = 48.dp

/**
 * Full-screen gate shown until [granted]; then it renders [content]. Stateless: the app owns the
 * permission state and picks [actionLabel] and [onAction] from [permissionGateMode]. Example
 * (accompanist in the app):
 *
 *     var requestedOnce by rememberSaveable { mutableStateOf(false) }
 *     val perms = rememberMultiplePermissionsState(listOf(CAMERA, RECORD_AUDIO)) { requestedOnce = true }
 *     val mode = permissionGateMode(perms.allPermissionsGranted, perms.shouldShowRationale, requestedOnce)
 *     PermissionGate(
 *         granted = mode == PermissionGateMode.Granted,
 *         title = stringResource(R.string.camera_permission_title),
 *         message = stringResource(R.string.camera_permission_message),
 *         actionLabel = stringResource(if (mode == PermissionGateMode.OpenSettings) R.string.open_settings else R.string.grant),
 *         onAction = { if (mode == PermissionGateMode.OpenSettings) openAppSettings(context) else perms.launchMultiplePermissionRequest() },
 *     ) { RecordContent() }
 */
@Composable
fun PermissionGate(
    granted: Boolean,
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable () -> Unit,
) {
    if (granted) {
        content()
    } else {
        Box(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(AppTheme.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(PermissionIconSize),
                    )
                }
                Text(
                    title.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                PrimaryButton(text = actionLabel, onClick = onAction)
            }
        }
    }
}
