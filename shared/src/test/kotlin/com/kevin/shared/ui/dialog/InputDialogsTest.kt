package com.kevin.shared.ui.dialog

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputDialogsTest {
    @Test fun blankTextCannotBeConfirmedUnlessAllowed() {
        assertFalse(textInputConfirmEnabled("", allowEmpty = false))
        assertFalse(textInputConfirmEnabled("   ", allowEmpty = false))
        assertTrue(textInputConfirmEnabled("Run", allowEmpty = false))
        assertTrue(textInputConfirmEnabled("", allowEmpty = true))
    }

    @Test fun resultIsTrimmed() {
        assertEquals("Morning run", textInputResult("  Morning run \n"))
        assertEquals("", textInputResult("   "))
    }

    @Test fun singleChoiceNeedsASelectionThatIsAnOption() {
        val options = listOf("CMJ", "Squat Jump")
        assertFalse(singleChoiceConfirmEnabled(null, options))
        assertFalse(singleChoiceConfirmEnabled("Drop Jump", options))
        assertTrue(singleChoiceConfirmEnabled("Squat Jump", options))
    }
}
