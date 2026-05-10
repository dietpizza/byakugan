package com.dietpizza.byakugan.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationModel(
    @PrimaryKey
    val id: String,
    val name: String,
    val path: String,
    val fileCount: Long,
)