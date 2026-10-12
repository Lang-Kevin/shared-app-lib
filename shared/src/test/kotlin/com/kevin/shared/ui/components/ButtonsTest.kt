package com.kevin.shared.ui.components

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ButtonsTest {
    private val t = ButtonTokens(
        accent = Color.Red, accentTint = Color.DarkGray, onSurface = Color.White,
        outlineVariant = Color.Gray, error = Color.Magenta, textDisabled = Color.LightGray,
    )

    @Test fun primaryIsAccentBorderPlusTint() {
        assertEquals(ButtonPalette(Color.DarkGray, Color.White, Color.Red), buttonPalette(ButtonKind.Primary, true, t))
    }

    @Test fun onlyPrimaryUsesTheAccent() {
        for (kind in listOf(ButtonKind.Secondary, ButtonKind.Destructive)) {
            val p = buttonPalette(kind, true, t)
            assertNotEquals(t.accent, p.border)
            assertNotEquals(t.accentTint, p.container)
        }
    }

    @Test fun secondaryIsHairline() {
        assertEquals(ButtonPalette(Color.Transparent, Color.White, Color.Gray), buttonPalette(ButtonKind.Secondary, true, t))
    }

    @Test fun destructiveUsesErrorWithSoftBorder() {
        assertEquals(
            ButtonPalette(Color.Transparent, Color.Magenta, Color.Magenta.copy(alpha = 0.45f)),
            buttonPalette(ButtonKind.Destructive, true, t),
        )
    }

    @Test fun disabledDropsAllColourForEveryKind() {
        for (kind in ButtonKind.entries) {
            assertEquals(ButtonPalette(Color.Transparent, Color.LightGray, Color.Gray), buttonPalette(kind, false, t))
        }
    }
}
