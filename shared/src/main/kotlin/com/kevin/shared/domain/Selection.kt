package com.kevin.shared.domain

/** Immutable multi-select state for list screens. */
data class Selection<K>(val ids: Set<K> = emptySet()) {
    val isActive: Boolean get() = ids.isNotEmpty()
    val size: Int get() = ids.size

    operator fun contains(id: K): Boolean = id in ids

    fun start(id: K): Selection<K> = Selection(setOf(id))

    fun toggle(id: K): Selection<K> = Selection(if (id in ids) ids - id else ids + id)

    fun clear(): Selection<K> = Selection()
}
