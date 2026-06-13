package com.kevin.shared.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object BleDevicePrefKeys {
    val SAVED_DEVICE_MAC = stringPreferencesKey("saved_device_mac")
    val SAVED_DEVICES    = stringPreferencesKey("saved_devices")
    val AUTO_CONNECT     = booleanPreferencesKey("auto_connect")
}
