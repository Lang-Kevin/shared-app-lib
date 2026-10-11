package com.kevin.shared.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** small = chips/fields, medium = cards (14dp), large = sheets/dialogs. */
val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)

/** Buttons, chips, status tags. Exposed as AppTheme.pillShape. */
internal val PillShape = RoundedCornerShape(50)
