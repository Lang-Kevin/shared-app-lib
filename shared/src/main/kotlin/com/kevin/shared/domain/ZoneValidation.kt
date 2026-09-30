package com.kevin.shared.domain

sealed interface ZoneTextError {
    data object InvalidNumber : ZoneTextError
    data object MinNotBelowMax : ZoneTextError
    /** [previousZone] is the 1-based number of the zone this one overlaps with (e.g. 1 for "Z1"). */
    data class OverlapsPrevious(val previousZone: Int) : ZoneTextError
}

// Per-zone error, or null if valid. Checks lo < hi and no overlap with the previous zone.
fun validateZoneTexts(zoneTexts: List<Pair<String, String>>): List<ZoneTextError?> =
    zoneTexts.mapIndexed { i, (loText, hiText) ->
        val lo = loText.toIntOrNull()
        val hi = hiText.toIntOrNull()
        val prevHi = if (i > 0) zoneTexts[i - 1].second.toIntOrNull() else null
        when {
            lo == null || hi == null -> ZoneTextError.InvalidNumber
            lo >= hi -> ZoneTextError.MinNotBelowMax
            prevHi != null && lo < prevHi -> ZoneTextError.OverlapsPrevious(i)
            else -> null
        }
    }
