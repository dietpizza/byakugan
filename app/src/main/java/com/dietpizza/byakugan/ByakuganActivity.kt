package com.dietpizza.byakugan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.composables.LibraryScreen
import com.dietpizza.byakugan.composables.dynamicColorScheme
import com.google.android.material.color.DynamicColors

class ByakuganActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            LibraryScreen(
                context = this,
                colorScheme = dynamicColorScheme(this),
                lifecycleScope = lifecycleScope,
            )
        }
    }
}
