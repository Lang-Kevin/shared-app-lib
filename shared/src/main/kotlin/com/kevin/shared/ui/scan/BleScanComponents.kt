package com.kevin.shared.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kevin.shared.R
import com.kevin.shared.ble.ConnectionState
import com.kevin.shared.domain.DeviceType
import com.kevin.shared.domain.DiscoveredDevice
import com.kevin.shared.domain.SavedDevice
import com.kevin.shared.ui.components.ListCard
import com.kevin.shared.ui.components.TextActionButton
import com.kevin.shared.ui.theme.AppTheme

@Composable
fun BleStatusCard(
    connectionState: ConnectionState,
    autoConnect: Boolean,
    onDisconnect: () -> Unit,
    onToggleAutoConnect: () -> Unit,
    deviceName: String? = null,
    detail: String? = null
) {
    val statusText = when (connectionState) {
        is ConnectionState.Reconnecting -> stringResource(R.string.shared_ble_status_reconnecting)
        is ConnectionState.Error -> stringResource(R.string.shared_ble_status_error, connectionState.reason)
        is ConnectionState.Disconnected -> stringResource(R.string.shared_ble_status_disconnected)
        is ConnectionState.Connecting -> stringResource(R.string.shared_ble_status_connecting)
        is ConnectionState.Connected -> stringResource(R.string.shared_ble_status_connected)
        is ConnectionState.Ready -> stringResource(R.string.shared_ble_status_ready)
    }
    val statusLabel = stringResource(R.string.shared_ble_status_label, statusText)
    val statusColor = if (connectionState is ConnectionState.Error)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.onSurface
    val dotColor = when {
        connectionState is ConnectionState.Ready -> AppTheme.colors.success
        connectionState is ConnectionState.Error -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val cardBorder = BorderStroke(
        1.dp,
        if (connectionState is ConnectionState.Ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    )
    val cardBg = if (connectionState is ConnectionState.Error)
        MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.weight(1f).padding(end = AppTheme.spacing.sm),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = cardBorder
        ) {
            Row(
                modifier = Modifier.padding(horizontal = AppTheme.spacing.md, vertical = AppTheme.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
                    .semantics { contentDescription = statusLabel })
                Spacer(Modifier.width(AppTheme.spacing.sm))
                Column(modifier = Modifier.weight(1f)) {
                    Text(statusLabel, style = MaterialTheme.typography.bodyMedium, color = statusColor)
                    listOfNotNull(deviceName, detail).forEach {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (connectionState !is ConnectionState.Disconnected) {
                    TextActionButton(text = stringResource(R.string.shared_ble_disconnect), onClick = onDisconnect)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.shared_ble_auto_connect).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(AppTheme.spacing.xs))
            Switch(checked = autoConnect, onCheckedChange = { onToggleAutoConnect() })
        }
    }
}

@Composable
fun SavedDeviceItem(device: SavedDevice, onClick: () -> Unit, onForget: () -> Unit) {
    ListCard(
        title = device.name,
        subtitle = device.address,
        onClick = onClick,
        trailing = { TextActionButton(text = stringResource(R.string.shared_ble_forget), onClick = onForget) },
    )
}

@Composable
fun DiscoveredDeviceItem(
    device: DiscoveredDevice,
    onClick: () -> Unit,
    // ponytail: null = default icon/subtitle; resolved here because stringResource needs a composable scope
    iconAndSubtitle: ((DiscoveredDevice) -> Pair<ImageVector, String>)? = null
) {
    val simulatedText = stringResource(R.string.shared_ble_simulated_device)
    val chestStrapText = stringResource(R.string.shared_ble_chest_strap)
    val (icon, subtitle) = iconAndSubtitle?.invoke(device) ?: when {
        device is DiscoveredDevice.Fake -> Icons.Default.Bluetooth to simulatedText
        device.deviceType == DeviceType.CHEST_STRAP -> Icons.Default.Favorite to chestStrapText
        else -> Icons.Default.Bluetooth to device.address
    }
    ListCard(
        title = device.displayName,
        subtitle = subtitle,
        onClick = onClick,
        leading = { Icon(icon, contentDescription = subtitle, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
    )
}
