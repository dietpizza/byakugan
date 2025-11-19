package com.dietpizza.byakugan.models

enum class SortBy {
    NAME,
    PAGES,
    TIME
}

enum class SortOrder {
    ASCENDING,
    DESCENDING
}

data class SortSettings(
    val sortBy: SortBy = SortBy.NAME,
    val sortOrder: SortOrder = SortOrder.ASCENDING
)
