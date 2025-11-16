package com.dietpizza.byakugan

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dietpizza.byakugan.databinding.ActivityMainBinding
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.services.StorageService
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val storageService = StorageService()

    // Register folder picker launcher
    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        val absolutePath = storageService.getFilePathFromUri(this, uri)

        if (absolutePath != null) {
            this.lifecycleScope.launch {
                val listOfMangaFuture = async {
                    MangaParserService.findMangaFiles(absolutePath);
                }

                val listOfManga = listOfMangaFuture.await()

                for (m in listOfManga) {
                    Log.i(TAG, "Manga ${m.filename}")
                }
            }
        } else {
            Log.i(TAG, "Folder Selection cancelled")
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