package com.dietpizza.byakugan

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContentView(R.layout.activity_main)

        // Setup settings button click listener
        findViewById<ImageButton>(R.id.settings_button).setOnClickListener {
            // Handle settings button click
        }
    }
}