package com.dietpizza.byakugan.models

data class MangaMetadataModel(
    val filename: String,
    val size: Long,
    val pageCount: Int,
    val coverImagePath: String?,
    val lastPage: Int?
)

