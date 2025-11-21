package com.dietpizza.byakugan.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.screens.library.LibraryScreen
import com.dietpizza.byakugan.dynamicColorScheme
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.google.android.material.color.DynamicColors

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val mangaLibraryViewmodel: MangaLibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display with transparent status bar
        enableEdgeToEdge()

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            LibraryScreen(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope,
                mangaLibraryViewmodel
            )
        }
    }
}
