package com.dietpizza.byakugan.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.screens.viewer.MangaViewerScreen
import com.dietpizza.byakugan.dynamicColorScheme
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import com.google.android.material.color.DynamicColors

private const val TAG = "MangaViewerActivity"

class MangaViewerActivity : ComponentActivity() {
    private val mangaPanelViewmodel: MangaPanelViewModel by viewModels()
    private val mangaLibraryViewmodel: MangaLibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        // Get manga file path from intent
        val mangaId = intent.getStringExtra("MANGA_ID") ?: ""

        setContent {
            MangaViewerScreen(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope,
                mangaId,
                mangaLibraryViewmodel,
                mangaPanelViewmodel
            )
        }
    }
}