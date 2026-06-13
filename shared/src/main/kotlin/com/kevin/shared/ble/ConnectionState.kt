package com.kevin.shared.ble

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting   : ConnectionState()
    object Connected    : ConnectionState()
    object Ready        : ConnectionState()
    object Reconnecting : ConnectionState()
    data class Error(val reason: String) : ConnectionState()
}
