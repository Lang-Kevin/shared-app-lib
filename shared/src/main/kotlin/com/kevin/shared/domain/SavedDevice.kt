package com.kevin.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class SavedDevice(val address: String, val name: String)
