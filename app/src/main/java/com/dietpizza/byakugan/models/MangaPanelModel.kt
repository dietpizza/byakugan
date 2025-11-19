package com.dietpizza.byakugan.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manga_panels")
data class MangaPanelModel(
    @PrimaryKey
    val id: String,
    val mangaId: String,
    val panelName: String,
    val height: Int,
    val width: Int,
    val aspectRatio: Float
)

