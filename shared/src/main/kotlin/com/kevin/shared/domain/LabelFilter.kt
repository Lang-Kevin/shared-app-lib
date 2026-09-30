package com.kevin.shared.domain

/** Chip filter: empty set = no filter (everything matches). */
data class LabelFilter(val selected: Set<String> = emptySet()) {
    val isActive: Boolean get() = selected.isNotEmpty()

    fun toggle(label: String): LabelFilter =
        LabelFilter(if (label in selected) selected - label else selected + label)

    fun matches(label: String): Boolean = !isActive || label in selected

    fun <T> apply(items: List<T>, labelOf: (T) -> String): List<T> =
        if (!isActive) items else items.filter { matches(labelOf(it)) }
}
