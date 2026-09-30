package com.kevin.shared.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kevin.shared.ui.theme.ConnectedGreen

@Composable
fun BleStatusCard(
    connectionState: ConnectionState,
    autoConnect: Boolean,
    onDisconnect: () -> Unit,
    onToggleAutoConnect: () -> Unit
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
        connectionState is ConnectionState.Ready -> ConnectedGreen
        connectionState is ConnectionState.Error -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val cardBorder = if (connectionState is ConnectionState.Ready)
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
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
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = cardBorder
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .size(8.dp)
                    .background(dotColor, CircleShape)
                    .semantics { contentDescription = statusLabel })
                Spacer(Modifier.width(8.dp))
                Text(
                    statusLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor,
                    modifier = Modifier.weight(1f)
                )
                if (connectionState !is ConnectionState.Disconnected) {
                    TextButton(onClick = onDisconnect) { Text(stringResource(R.string.shared_ble_disconnect)) }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.shared_ble_auto_connect), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.width(4.dp))
            Switch(checked = autoConnect, onCheckedChange = { onToggleAutoConnect() })
        }
    }
}

@Composable
fun SavedDeviceItem(device: SavedDevice, onClick: () -> Unit, onForget: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).clickable(onClick = onClick)) {
                Text(device.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onForget) { Text(stringResource(R.string.shared_ble_forget)) }
        }
    }
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
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = subtitle)
            Column {
                Text(device.displayName, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
