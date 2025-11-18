package com.dietpizza.byakugan.composables

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.services.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val BYAKUGAN_TAG = "ByakuganApp"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ByakuganApp(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    storageService: StorageService,
    onSettingsClick: () -> Unit = {}
) {
    var mangaList by remember { mutableStateOf<List<MangaMetadataModel>>(emptyList()) }

    // Register permission launcher for MANAGE_EXTERNAL_STORAGE
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (Environment.isExternalStorageManager()) {
            Log.i(BYAKUGAN_TAG, "Storage permission granted")
        } else {
            Log.w(BYAKUGAN_TAG, "Storage permission denied")
        }
    }

    // Register folder picker launcher
    val folderPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        val absolutePath = storageService.getFilePathFromUri(context, uri)
        Log.i(BYAKUGAN_TAG, "Absolute path: $absolutePath")

        if (absolutePath != null) {
            lifecycleScope.launch {
                Log.i(BYAKUGAN_TAG, "Fetching manga list")
                // Perform file I/O on IO dispatcher to avoid blocking UI thread
                val listOfManga = withContext(Dispatchers.IO) {
                    MangaParserService.findMangaFiles(absolutePath, context)
                }

                // Switch back to Main dispatcher for UI updates
                withContext(Dispatchers.Main) {
                    mangaList = listOfManga
                }

                Log.i(BYAKUGAN_TAG, "All mangas in folder $listOfManga")
                for (m in listOfManga) {
                    Log.i(BYAKUGAN_TAG, "Manga ${m.filename}")
                }
            }
        } else {
            Log.i(BYAKUGAN_TAG, "Folder Selection cancelled")
        }
    }

    // Check and request storage permission on composition
    LaunchedEffect(Unit) {
        checkAndRequestStoragePermission(context, storagePermissionLauncher)
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Scaffold(
            topBar = {
                ByakuganTopBar(
                    onSettingsClick = onSettingsClick
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (mangaList.isNotEmpty()) {
                    MangaGrid(mangaList = mangaList)
                } else {
                    EmptyState(
                        onOpenFolderClick = {
                            folderPickerLauncher.launch(null)
                        }
                    )
                }
            }
        }
    }
}

private fun checkAndRequestStoragePermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    // Android 11+ requires MANAGE_EXTERNAL_STORAGE
    if (!Environment.isExternalStorageManager()) {
        Log.i(BYAKUGAN_TAG, "Requesting MANAGE_EXTERNAL_STORAGE permission")
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = "package:${context.packageName}".toUri()
            launcher.launch(intent)
        } catch (e: Exception) {
            Log.e(BYAKUGAN_TAG, "Error requesting storage permission", e)
            // Fallback to general settings if specific intent fails
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            launcher.launch(intent)
        }
    } else {
        Log.i(BYAKUGAN_TAG, "MANAGE_EXTERNAL_STORAGE permission already granted")
    }
}

@Composable
fun dynamicColorScheme(context: Context): ColorScheme {
    return dynamicDarkColorScheme(context)
}
