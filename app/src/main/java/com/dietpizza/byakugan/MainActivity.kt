package com.dietpizza.byakugan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dietpizza.byakugan.databinding.ActivityMainBinding
import com.dietpizza.byakugan.services.StorageService
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val storageService = StorageService()

    // Register folder picker launcher
    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.i("MainActivity", "Selected folder URI: $uri")
            val absolutePath = storageService.getFilePathFromUri(this, uri)
            Log.i("MainActivity", "Absolute path: $absolutePath")
        } else {
            Log.i("MainActivity", "Folder selection cancelled")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        // Inflate view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup settings button click listener
        binding.settingsButton.setOnClickListener {
            // Handle settings button click
        }

        // Setup folder picker button click listener
        binding.folderPickerButton.setOnClickListener {
            folderPickerLauncher.launch(null)
        }
    }
}