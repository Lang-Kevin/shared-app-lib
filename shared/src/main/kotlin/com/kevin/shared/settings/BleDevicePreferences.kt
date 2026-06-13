package com.kevin.shared.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kevin.shared.domain.SavedDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val DataStore<Preferences>.savedDeviceAddressFlow: Flow<String?>
    get() = data.map { it[BleDevicePrefKeys.SAVED_DEVICE_MAC] }

suspend fun DataStore<Preferences>.saveDeviceAddress(address: String) {
    edit { it[BleDevicePrefKeys.SAVED_DEVICE_MAC] = address }
}

suspend fun DataStore<Preferences>.clearSavedDevice() {
    edit { it.remove(BleDevicePrefKeys.SAVED_DEVICE_MAC) }
}

val DataStore<Preferences>.savedDevicesFlow: Flow<List<SavedDevice>>
    get() = data.map { prefs ->
        prefs[BleDevicePrefKeys.SAVED_DEVICES]?.let { json ->
            runCatching { Json.decodeFromString<List<SavedDevice>>(json) }.getOrDefault(emptyList())
        } ?: emptyList()
    }

suspend fun DataStore<Preferences>.addSavedDevice(device: SavedDevice) {
    edit { prefs ->
        val current = prefs[BleDevicePrefKeys.SAVED_DEVICES]?.let {
            runCatching { Json.decodeFromString<List<SavedDevice>>(it) }.getOrDefault(emptyList())
        } ?: emptyList()
        prefs[BleDevicePrefKeys.SAVED_DEVICES] = Json.encodeToString(
            listOf(device) + current.filter { it.address != device.address }
        )
    }
}

suspend fun DataStore<Preferences>.removeSavedDevice(address: String) {
    edit { prefs ->
        val current = prefs[BleDevicePrefKeys.SAVED_DEVICES]?.let {
            runCatching { Json.decodeFromString<List<SavedDevice>>(it) }.getOrDefault(emptyList())
        } ?: emptyList()
        prefs[BleDevicePrefKeys.SAVED_DEVICES] = Json.encodeToString(
            current.filter { it.address != address }
        )
    }
}

val DataStore<Preferences>.autoConnectFlow: Flow<Boolean>
    get() = data.map { it[BleDevicePrefKeys.AUTO_CONNECT] ?: false }

suspend fun DataStore<Preferences>.setAutoConnect(enabled: Boolean) {
    edit { it[BleDevicePrefKeys.AUTO_CONNECT] = enabled }
}
