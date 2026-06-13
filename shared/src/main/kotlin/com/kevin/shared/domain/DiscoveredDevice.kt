package com.kevin.shared.domain

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.kevin.shared.ble.BleConstants

sealed class DiscoveredDevice {
    abstract val address: String
    abstract val displayName: String
    abstract val deviceType: DeviceType

    data class Real(val scanResult: ScanResult) : DiscoveredDevice() {
        @get:SuppressLint("MissingPermission")
        override val address: String get() = scanResult.device.address
        @get:SuppressLint("MissingPermission")
        override val displayName: String get() = scanResult.device.name ?: "Unbekanntes Gerät"
        @get:SuppressLint("MissingPermission")
        override val deviceType: DeviceType get() = guessDeviceType(scanResult.device.name)
    }

    // Pass the app-specific display name (e.g. "Pseudo-Sensor [Test]" or "Pseudo-MPU6050 [Test]")
    data class Fake(override val displayName: String = "Test Device") : DiscoveredDevice() {
        override val address: String = BleConstants.FAKE_DEVICE_ADDRESS
        override val deviceType: DeviceType = DeviceType.UNKNOWN
    }
}
