package com.kevin.shared.domain

// Per-zone error, or null if valid. Checks lo < hi and no overlap with the previous zone.
fun validateZoneTexts(zoneTexts: List<Pair<String, String>>): List<String?> =
    zoneTexts.mapIndexed { i, (loText, hiText) ->
        val lo = loText.toIntOrNull()
        val hi = hiText.toIntOrNull()
        val prevHi = if (i > 0) zoneTexts[i - 1].second.toIntOrNull() else null
        when {
            lo == null || hi == null -> "Ungültige Zahl"
            lo >= hi -> "Min muss kleiner als Max sein"
            prevHi != null && lo < prevHi -> "Überlappt mit Z$i"
            else -> null
        }
    }
