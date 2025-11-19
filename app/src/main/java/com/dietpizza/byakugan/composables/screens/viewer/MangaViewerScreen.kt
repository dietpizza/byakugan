package com.dietpizza.byakugan.composables.screens.viewer

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

@Composable
fun MangaViewerScreen(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    mangaFilePath: String,
    startPage: Int
) {

    LaunchedEffect(Unit) {
        // TODO: Add all the startup shit
        // mangaFilePath contains the path to the manga file
        // startPage contains the last read page (or 0 if starting fresh)
    }

    Column {
        // TODO: Add ui
    }
}