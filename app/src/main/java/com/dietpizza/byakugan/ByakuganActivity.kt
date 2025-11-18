package com.dietpizza.byakugan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.ByakuganApp
import com.dietpizza.byakugan.composables.dynamicColorScheme
import com.dietpizza.byakugan.services.StorageService
import com.google.android.material.color.DynamicColors

class ByakuganActivity : ComponentActivity() {

    private val storageService = StorageService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            ByakuganApp(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope = lifecycleScope,
                storageService = storageService,
                onSettingsClick = {
                    // Handle settings button click
                }
            )
        }
    }
}
