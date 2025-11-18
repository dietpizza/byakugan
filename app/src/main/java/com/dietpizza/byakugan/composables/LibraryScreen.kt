package com.dietpizza.byakugan.composables

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.dietpizza.byakugan.services.MangaLibraryService
import com.dietpizza.byakugan.services.StorageService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "LibraryScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    viewModel: MangaLibraryViewModel
) {
    // Collect manga list from database reactively
    val mangaList by viewModel.allManga.collectAsState(initial = emptyList())

    // Register permission launcher for MANAGE_EXTERNAL_STORAGE
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        Log.i(
            TAG,
            "Is External Storage Permission Granted ${Environment.isExternalStorageManager()}"
        )
    }

    // Register folder picker launcher
    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        val absolutePath = StorageService.getFilePathFromUri(context, uri)
        Log.i(TAG, "Absolute path: $absolutePath")

        if (absolutePath != null) {
            // Save the selected folder path to preferences
            MangaLibraryService.saveMangaFolderPath(context, absolutePath)

            // Scan folder and update database
            lifecycleScope.launch {
                MangaLibraryService.scanFolderAndUpdateDatabase(absolutePath, context, viewModel)
            }
        }
    }

    val onSettingsClick: () -> Unit = {
        // TODO: Add settings dialog code
    }

    val onOpenFolderClick: () -> Unit = {
        folderPickerLauncher.launch(null)
    }

    // Check and request storage permission on composition
    LaunchedEffect(Unit) {
        StorageService.checkAndRequestStoragePermission(context, storagePermissionLauncher)

        val savedPath = MangaLibraryService.getSavedMangaFolderPath(context)

        if (savedPath != null) {
            lifecycleScope.launch {
                MangaLibraryService.scanFolderAndUpdateDatabase(savedPath, context, viewModel)
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    onSettingsClick
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LibraryGrid(mangaList, onOpenFolderClick)
            }
        }
    }
}

@Composable
fun dynamicColorScheme(context: Context): ColorScheme {
    return dynamicDarkColorScheme(context)
}
