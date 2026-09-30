package com.kevin.shared.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LabelFilterTest {

    private data class Item(val name: String, val label: String)

    private val items = listOf(Item("a", "Run"), Item("b", "Bike"), Item("c", "Run"), Item("d", "Swim"))

    @Test
    fun `empty filter is inactive and matches everything`() {
        val filter = LabelFilter()
        assertFalse(filter.isActive)
        assertTrue(filter.matches("Run"))
        assertTrue(filter.matches(""))
    }

    @Test
    fun `empty filter apply returns the input unchanged`() {
        assertSame(items, LabelFilter().apply(items) { it.label })
    }

    @Test
    fun `toggle adds and removes labels`() {
        val filter = LabelFilter().toggle("Run").toggle("Bike")
        assertEquals(setOf("Run", "Bike"), filter.selected)
        assertTrue(filter.isActive)

        val removed = filter.toggle("Run")
        assertEquals(setOf("Bike"), removed.selected)
        assertFalse(removed.toggle("Bike").isActive)
    }

    @Test
    fun `active filter matches only selected labels`() {
        val filter = LabelFilter(setOf("Run"))
        assertTrue(filter.matches("Run"))
        assertFalse(filter.matches("Bike"))
    }

    @Test
    fun `apply keeps only items with selected labels in order`() {
        val filtered = LabelFilter(setOf("Run", "Swim")).apply(items) { it.label }
        assertEquals(listOf("a", "c", "d"), filtered.map { it.name })
    }
}
