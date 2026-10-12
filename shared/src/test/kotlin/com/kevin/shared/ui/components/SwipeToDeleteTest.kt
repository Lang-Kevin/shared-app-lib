package com.kevin.shared.ui.components

import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeToDeleteTest {
    @Test fun onlyEndToStartRequestsDelete() {
        assertTrue(shouldRequestDelete(SwipeToDismissBoxValue.EndToStart, enabled = true))
        assertFalse(shouldRequestDelete(SwipeToDismissBoxValue.StartToEnd, enabled = true))
        assertFalse(shouldRequestDelete(SwipeToDismissBoxValue.Settled, enabled = true))
    }

    @Test fun disabledNeverRequestsDelete() {
        assertFalse(shouldRequestDelete(SwipeToDismissBoxValue.EndToStart, enabled = false))
    }

    @Test fun thresholdIsFortyPercentOfTheRow() {
        assertEquals(400f, swipeDeleteThreshold(1000f), 0.001f)
    }

    @Test fun backgroundOnlyWhileSwipingTowardsStart() {
        assertTrue(showDeleteBackground(SwipeToDismissBoxValue.EndToStart))
        assertFalse(showDeleteBackground(SwipeToDismissBoxValue.Settled))
        assertFalse(showDeleteBackground(SwipeToDismissBoxValue.StartToEnd))
    }

    @Test fun containerIsTranslucentError() {
        assertEquals(Color.Red.copy(alpha = 0.18f), swipeDeleteContainer(Color.Red))
    }
}
