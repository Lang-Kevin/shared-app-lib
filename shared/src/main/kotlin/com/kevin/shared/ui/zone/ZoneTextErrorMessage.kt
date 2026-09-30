package com.kevin.shared.ui.zone

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kevin.shared.R
import com.kevin.shared.domain.ZoneTextError

/** Localized text for a zone validation error. */
@Composable
fun ZoneTextError.message(): String = when (this) {
    ZoneTextError.InvalidNumber -> stringResource(R.string.shared_zone_error_invalid_number)
    ZoneTextError.MinNotBelowMax -> stringResource(R.string.shared_zone_error_min_not_below_max)
    is ZoneTextError.OverlapsPrevious -> stringResource(R.string.shared_zone_error_overlaps, previousZone)
}
