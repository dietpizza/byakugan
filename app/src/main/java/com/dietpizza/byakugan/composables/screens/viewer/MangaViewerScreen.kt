package com.dietpizza.byakugan.composables.screens.viewer

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import kotlinx.coroutines.CoroutineScope

private const val TAG = "MangaViewerScreen"

@Composable
fun MangaViewerScreen(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    mangaId: String,
    mangaPanelViewmodel: MangaPanelViewModel
) {
    val mangaPanels by mangaPanelViewmodel.getPanelsForManga(mangaId).collectAsState(initial = null)

    LaunchedEffect(mangaPanels) {
        Log.e(TAG, "MangaPanels ${mangaPanels}")
    }

    Column {
        // TODO: Add ui
    }
}