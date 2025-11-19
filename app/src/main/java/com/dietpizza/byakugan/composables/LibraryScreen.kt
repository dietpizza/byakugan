package com.dietpizza.byakugan.composables

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.dietpizza.byakugan.models.SortSettings
import com.dietpizza.byakugan.services.MangaLibraryService
import com.dietpizza.byakugan.services.StorageService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
    val currentSortSettings by viewModel.sortSettings.collectAsState(initial = SortSettings())

    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    // Sort settings dialog state
    var showSortDialog by remember { mutableStateOf(false) }

    val refreshLibrary: (dir: String) -> Unit = { dir ->
        lifecycleScope.launch {
            isRefreshing = true
            MangaLibraryService.scanFolderAndUpdateDatabase(dir, context, viewModel) {
                isRefreshing = false
            }
        }
    }

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
        val dirPath = StorageService.getFilePathFromUri(context, uri)

        if (dirPath != null) {
            MangaLibraryService.saveMangaFolderPath(context, dirPath)
            refreshLibrary(dirPath)
        }
    }


    val onSettingsClick: () -> Unit = {
        showSortDialog = true
    }

    val onOpenFolderClick: () -> Unit = {
        folderPickerLauncher.launch(null)
    }

    val onRefresh: () -> Unit = {
        val savedPath = MangaLibraryService.getSavedMangaFolderPath(context)
        if (savedPath != null) refreshLibrary(savedPath)
    }

    // Check and request storage permission on composition
    LaunchedEffect(Unit) {
        StorageService.checkAndRequestStoragePermission(context, storagePermissionLauncher)

        val savedPath = MangaLibraryService.getSavedMangaFolderPath(context)
        if (savedPath != null) refreshLibrary(savedPath)
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
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = onRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                // Custom Material 3 styled indicator
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                if (!isRefreshing)
                    LibraryGrid(mangaList, onOpenFolderClick)
            }
        }

        // Sort settings dialog
        if (showSortDialog) {
            SortSettingsDialog(
                currentSettings = currentSortSettings,
                onDismiss = { showSortDialog = false },
                onConfirm = { settings ->
                    viewModel.updateSortSettings(settings)
                }
            )
        }
    }
}

@Composable
fun dynamicColorScheme(context: Context): ColorScheme {
    return dynamicDarkColorScheme(context)
}
