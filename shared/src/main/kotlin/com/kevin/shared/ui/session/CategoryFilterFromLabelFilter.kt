package com.kevin.shared.ui.session

import androidx.compose.runtime.Composable
import com.kevin.shared.domain.LabelFilter

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    filter: LabelFilter,
    onToggle: (String) -> Unit
) {
    CategoryFilterRow(categories, filter.selected, onToggle)
}
