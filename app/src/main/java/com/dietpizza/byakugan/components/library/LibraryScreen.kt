package com.dietpizza.byakugan.components.library

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.components.ui.AppBar
import com.dietpizza.byakugan.models.SortSettings
import com.dietpizza.byakugan.services.MangaLibraryService
import com.dietpizza.byakugan.services.StorageService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "LibraryScreen"

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    mangaLibraryViewmodel: MangaLibraryViewModel
) {
    // Collect manga list from database reactively
    val mangaList by mangaLibraryViewmodel.allManga.collectAsState(initial = null)
    val currentSortSettings by mangaLibraryViewmodel.sortSettings.collectAsState(initial = SortSettings())

    // TopAppBar scroll behavior
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Pull-to-refresh state
    val pullToRefreshState = rememberPullToRefreshState()

    // Refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    var parserProgress by remember { mutableFloatStateOf(0f) }

    // Sort settings dialog state
    var showSortDialog by remember { mutableStateOf(false) }

    val refreshLibrary: (dir: String) -> Unit = { dir ->
        lifecycleScope.launch {
            isRefreshing = true
            MangaLibraryService.scanFolderAndUpdateDatabase(
                dir, context, mangaLibraryViewmodel,
                onComplete = {
                    isRefreshing = false
                },
                onProgress = { progress ->
                    parserProgress = progress
                })
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

    val onRefreshClick: () -> Unit = {
        val savedPath = MangaLibraryService.getSavedMangaFolderPath(context)
        if (savedPath != null) refreshLibrary(savedPath)
    }

    // Check and request storage permission on composition
    LaunchedEffect(Unit) {
        StorageService.checkAndRequestStoragePermission(context, storagePermissionLauncher)
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    "Your Library",
                    onSettingsClick,
                    onRefreshClick,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PullToRefreshBox(
                    state = pullToRefreshState,
                    onRefresh = onRefreshClick,
                    isRefreshing = isRefreshing,
                    indicator = {
                        CustomPullToRefreshIndicator(
                            pullToRefreshState,
                            isRefreshing,
                            parserProgress,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
                    LibraryGrid(mangaList, isRefreshing, onOpenFolderClick)
                }
            }
        }

        // Sort settings dialog
        if (showSortDialog) {
            SortSettingsDialog(
                currentSettings = currentSortSettings,
                onDismiss = { showSortDialog = false },
                onConfirm = { settings ->
                    mangaLibraryViewmodel.updateSortSettings(settings)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    PullToRefreshDefaults.IndicatorBox(
        state,
        isRefreshing,
        modifier = modifier.then(Modifier.size(48.dp)),
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isRefreshing) {
                CircularWavyProgressIndicator(
                    progress = { progress / 100 },
                    modifier = Modifier.size(28.dp),
                    trackStroke = Stroke(width = 4.dp.value),
                    stroke = Stroke(width = 6.dp.value)

                )
            } else {
                CircularWavyProgressIndicator(
                    progress = { state.distanceFraction },
                    modifier = Modifier.size(28.dp),
                    trackStroke = Stroke(width = 4.dp.value),
                    stroke = Stroke(width = 6.dp.value)
                )
            }
        }
    }
}

