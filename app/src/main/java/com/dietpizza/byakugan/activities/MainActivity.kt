package com.dietpizza.byakugan.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.LibraryScreen
import com.dietpizza.byakugan.composables.dynamicColorScheme
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.google.android.material.color.DynamicColors

private const val TAG = "ByakuganActivity"

class MainActivity : ComponentActivity() {
    private val viewModel: MangaLibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            LibraryScreen(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope = lifecycleScope,
                viewModel = viewModel
            )
        }
    }
}
