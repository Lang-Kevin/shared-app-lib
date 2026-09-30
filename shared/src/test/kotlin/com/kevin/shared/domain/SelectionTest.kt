package com.kevin.shared.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionTest {

    @Test
    fun `default selection is empty and inactive`() {
        val selection = Selection<Long>()
        assertFalse(selection.isActive)
        assertEquals(0, selection.size)
        assertFalse(1L in selection)
    }

    @Test
    fun `start selects exactly the given id`() {
        val selection = Selection(setOf(1L, 2L)).start(3L)
        assertEquals(setOf(3L), selection.ids)
        assertTrue(selection.isActive)
        assertEquals(1, selection.size)
        assertTrue(3L in selection)
        assertFalse(1L in selection)
    }

    @Test
    fun `toggle adds and removes ids`() {
        val added = Selection<Long>().start(1L).toggle(2L)
        assertEquals(setOf(1L, 2L), added.ids)
        assertEquals(2, added.size)

        val removed = added.toggle(1L)
        assertEquals(setOf(2L), removed.ids)
        assertFalse(1L in removed)
    }

    @Test
    fun `toggling the last id deactivates the selection`() {
        val selection = Selection<Long>().start(1L).toggle(1L)
        assertFalse(selection.isActive)
        assertEquals(0, selection.size)
    }

    @Test
    fun `clear empties the selection`() {
        val selection = Selection(setOf(1L, 2L, 3L)).clear()
        assertEquals(emptySet<Long>(), selection.ids)
        assertFalse(selection.isActive)
    }
}
