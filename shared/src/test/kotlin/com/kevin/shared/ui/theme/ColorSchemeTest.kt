package com.kevin.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorSchemeTest {
    private val s = Brand.Default.toColorScheme()
    private val red = Color(0xFFE8202E)

    @Test fun specSlots() {
        assertEquals(red, s.primary)
        assertEquals(Color(0xFFFFFFFF), s.onPrimary)
        assertEquals(lerp(Color(0xFF0A0A0A), red, 0.09f), s.primaryContainer)
        assertEquals(Color(0xFFA3A3AB), s.secondary)
        assertEquals(red, s.tertiary)
        assertEquals(Color(0xFFFF5A4F), s.error)
        assertEquals(Color(0xFF000000), s.background)
        assertEquals(Color(0xFF0B0B0D), s.surface)
        assertEquals(Color(0xFF1F1F23), s.surfaceVariant)
        assertEquals(Color(0xFF1F1F23), s.outline)
        assertEquals(Color(0xFF2A2A2F), s.outlineVariant)
        assertEquals(Color(0xFFF2F2F4), s.onBackground)
        assertEquals(Color(0xFFF2F2F4), s.onSurface)
        assertEquals(Color(0xFF8D8D94), s.onSurfaceVariant)
    }

    // Slots M3 components read (dialogs, cards, chips, snackbars) must not fall back to the purple baseline.
    @Test fun noBaselineSlotsLeak() {
        assertEquals(Color.Transparent, s.surfaceTint)
        assertEquals(Color(0xFF2A2A2F), s.secondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onSecondaryContainer)
        assertEquals(Color(0xFFF2F2F4), s.onPrimaryContainer)
        assertEquals(Color(0xFF000000), s.surfaceContainerLowest)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainerLow)
        assertEquals(Color(0xFF0B0B0D), s.surfaceContainer)
        assertEquals(Color(0xFF141417), s.surfaceContainerHigh)
        assertEquals(Color(0xFF1F1F23), s.surfaceContainerHighest)
        assertEquals(Color(0xFFF2F2F4), s.inverseSurface)
        assertEquals(Color(0xFF0B0B0D), s.inverseOnSurface)
    }

    @Test fun fixedSlotsFollowAccent() {
        val blue = Color(0xFF2E6BE8)
        val b = Brand(accent = blue).toColorScheme()
        assertEquals(blue, b.primaryFixed)
        assertEquals(blue, b.tertiaryFixed)
        assertEquals(b.primaryContainer, b.primaryFixedDim)
    }

    // ponytail: M3 1.4.0 ColorScheme getters listed by hand because kotlin-reflect is not on the test
    // classpath. A new M3 Color slot must be added to this list manually, otherwise it goes unchecked.
    @Test fun noSlotKeepsM3Default() {
        val m3 = darkColorScheme()
        val slots: List<Pair<String, (ColorScheme) -> Color>> = listOf(
            "primary" to { it.primary }, "onPrimary" to { it.onPrimary },
            "primaryContainer" to { it.primaryContainer }, "onPrimaryContainer" to { it.onPrimaryContainer },
            "inversePrimary" to { it.inversePrimary },
            "secondary" to { it.secondary }, "onSecondary" to { it.onSecondary },
            "secondaryContainer" to { it.secondaryContainer }, "onSecondaryContainer" to { it.onSecondaryContainer },
            "tertiary" to { it.tertiary }, "onTertiary" to { it.onTertiary },
            "tertiaryContainer" to { it.tertiaryContainer }, "onTertiaryContainer" to { it.onTertiaryContainer },
            "background" to { it.background }, "onBackground" to { it.onBackground },
            "surface" to { it.surface }, "onSurface" to { it.onSurface },
            "surfaceVariant" to { it.surfaceVariant }, "onSurfaceVariant" to { it.onSurfaceVariant },
            "surfaceTint" to { it.surfaceTint },
            "inverseSurface" to { it.inverseSurface }, "inverseOnSurface" to { it.inverseOnSurface },
            "error" to { it.error }, "onError" to { it.onError },
            "errorContainer" to { it.errorContainer }, "onErrorContainer" to { it.onErrorContainer },
            "outline" to { it.outline }, "outlineVariant" to { it.outlineVariant },
            "scrim" to { it.scrim },
            "surfaceBright" to { it.surfaceBright }, "surfaceDim" to { it.surfaceDim },
            "surfaceContainerLowest" to { it.surfaceContainerLowest }, "surfaceContainerLow" to { it.surfaceContainerLow },
            "surfaceContainer" to { it.surfaceContainer }, "surfaceContainerHigh" to { it.surfaceContainerHigh },
            "surfaceContainerHighest" to { it.surfaceContainerHighest },
            "primaryFixed" to { it.primaryFixed }, "primaryFixedDim" to { it.primaryFixedDim },
            "onPrimaryFixed" to { it.onPrimaryFixed }, "onPrimaryFixedVariant" to { it.onPrimaryFixedVariant },
            "secondaryFixed" to { it.secondaryFixed }, "secondaryFixedDim" to { it.secondaryFixedDim },
            "onSecondaryFixed" to { it.onSecondaryFixed }, "onSecondaryFixedVariant" to { it.onSecondaryFixedVariant },
            "tertiaryFixed" to { it.tertiaryFixed }, "tertiaryFixedDim" to { it.tertiaryFixedDim },
            "onTertiaryFixed" to { it.onTertiaryFixed }, "onTertiaryFixedVariant" to { it.onTertiaryFixedVariant },
        )
        // Slots whose Night Drive value legitimately equals the M3 default. Exact match: a stale entry fails too.
        val expectedSame = setOf(
            "scrim", // Color.Black in both M3 and Night Drive (overlay black, by design)
        )
        val same = slots.filter { (_, get) -> get(s) == get(m3) }.map { it.first }.toSet()
        assertEquals(expectedSame, same)
    }

    @Test fun customAccentRecolorsAccentSlotsOnly() {
        val blue = Color(0xFF2E6BE8)
        val b = Brand(accent = blue).toColorScheme()
        assertEquals(blue, b.primary)
        assertEquals(blue, b.tertiary)
        assertEquals(lerp(Color(0xFF0A0A0A), blue, 0.09f), b.primaryContainer)
        assertEquals(s.background, b.background)
        assertEquals(s.error, b.error)
    }

    @Test @Suppress("DEPRECATION") fun deprecatedValsKeepOldValues() {
        assertEquals(Color(0xFFB6A6F2), PrimaryPurple)
        assertEquals(Color(0xFF4CAF82), ConnectedGreen)
        assertEquals(5, ZoneColors.size)
    }
}
