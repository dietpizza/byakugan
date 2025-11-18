package com.dietpizza.byakugan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.dietpizza.byakugan.adapters.MangaCardAdapter
import com.dietpizza.byakugan.databinding.ActivityMainBinding
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.services.StorageService
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainActivityContext = this

    // Register permission launcher for MANAGE_EXTERNAL_STORAGE
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (Environment.isExternalStorageManager()) {
            Log.i(TAG, "Storage permission granted")
        } else {
            Log.w(TAG, "Storage permission denied")
        }
    }

    // Register folder picker launcher
    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        val absolutePath = StorageService.getFilePathFromUri(mainActivityContext, uri)
        Log.i(TAG, "Absolute path: ${absolutePath}")


        if (absolutePath != null) {
            mainActivityContext.lifecycleScope.launch {
                Log.i(TAG, "Fetching manga list")
                // Perform file I/O on IO dispatcher to avoid blocking UI thread
                val listOfManga = withContext(Dispatchers.IO) {
                    MangaParserService.findMangaFiles(absolutePath, mainActivityContext)
                }

                // Switch back to Main dispatcher for UI updates
                withContext(Dispatchers.Main) {
                    setupMangaGridView(listOfManga)
                }

                Log.i(TAG, "All mangas in folder ${listOfManga}")
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
        DynamicColors.applyToActivityIfAvailable(mainActivityContext)

        // Inflate view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check and request storage permission
        checkAndRequestStoragePermission()

        // Setup settings button click listener
        binding.settingsButton.setOnClickListener {
            // Handle settings button click
        }

        // Setup folder picker button click listener
        binding.folderPickerButton.setOnClickListener {
            folderPickerLauncher.launch(null)
        }
    }

    private fun checkAndRequestStoragePermission() {
        // Android 11+ requires MANAGE_EXTERNAL_STORAGE
        if (!Environment.isExternalStorageManager()) {
            Log.i(TAG, "Requesting MANAGE_EXTERNAL_STORAGE permission")
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = "package:$packageName".toUri()
                storagePermissionLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting storage permission", e)
                // Fallback to general settings if specific intent fails
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                storagePermissionLauncher.launch(intent)
            }
        } else {
            Log.i(TAG, "MANAGE_EXTERNAL_STORAGE permission already granted")
        }
    }

    fun setupMangaGridView(listOfManga: List<MangaMetadataModel>) {
        // Setup RecyclerView with GridLayoutManager (2 columns)
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(mainActivityContext, 2)

        // Create and set adapter with manga list
        val adapter = MangaCardAdapter(listOfManga)
        binding.imagesRecyclerView.adapter = adapter

        // Show RecyclerView if there are manga files
        binding.imagesRecyclerView.visibility = if (listOfManga.isNotEmpty()) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
        // Hide empty state if there are manga files
        binding.emptyLayout.visibility = if (listOfManga.isNotEmpty()) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }

        Log.i(TAG, "Populated manga grid with ${listOfManga.size} items")
    }
}