package com.kevin.shared.ui.theme

import androidx.compose.ui.graphics.Color

// Pre-Night-Drive palette. Kept with the old values until hr/jump migrate (sub-projects 5/6); removed in sub-project 7.
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.primary", "androidx.compose.material3.MaterialTheme"))
val PrimaryPurple  = Color(0xFFB6A6F2)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.secondary", "androidx.compose.material3.MaterialTheme"))
val SecondaryBlue  = Color(0xFF5BA9E6)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.tertiary", "androidx.compose.material3.MaterialTheme"))
val TertiaryPink   = Color(0xFFF4738E)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.error", "androidx.compose.material3.MaterialTheme"))
val ErrorRed       = Color(0xFFEF4444)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.background", "androidx.compose.material3.MaterialTheme"))
val BackgroundDark = Color(0xFF1D1B20)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.surface", "androidx.compose.material3.MaterialTheme"))
val SurfaceDark    = Color(0xFF252330)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.onPrimary", "androidx.compose.material3.MaterialTheme"))
val OnPrimary      = Color(0xFF0B0912)
@Deprecated("Use the theme", ReplaceWith("MaterialTheme.colorScheme.primaryContainer", "androidx.compose.material3.MaterialTheme"))
val LightPurple    = Color(0xFFCBBCFF)
@Deprecated("Use the theme", ReplaceWith("AppTheme.colors.success", "com.kevin.shared.ui.theme.AppTheme"))
val ConnectedGreen = Color(0xFF4CAF82)

@Deprecated("Zones derive from the accent", ReplaceWith("AppTheme.colors.zones", "com.kevin.shared.ui.theme.AppTheme"))
val ZoneColors = listOf(
    Color(0xFF5BA9E6), // Z1
    Color(0xFF88C5EE), // Z2
    Color(0xFFCBBCFF), // Z3
    Color(0xFFE48AD6), // Z4
    Color(0xFFF4738E)  // Z5
)
