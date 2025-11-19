package com.dietpizza.byakugan.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.screens.viewer.MangaViewerScreen
import com.dietpizza.byakugan.dynamicColorScheme
import com.google.android.material.color.DynamicColors

class MangaViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        // Get manga file path from intent
        val mangaFilePath = intent.getStringExtra("MANGA_FILE_PATH") ?: ""
        val lastPage = intent.getIntExtra("LAST_PAGE", 0)

        setContent {
            MangaViewerScreen(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope = lifecycleScope,
                mangaFilePath = mangaFilePath,
                startPage = lastPage
            )
        }
    }
}