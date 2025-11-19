package com.dietpizza.byakugan.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_metadata")
data class MangaMetadataModel(
    @PrimaryKey
    val filename: String,
    val size: Long,
    val pageCount: Int,
    val coverImagePath: String?,
    val lastPage: Int?,
    val timestamp: Long?
)

