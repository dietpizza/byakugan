package com.dietpizza.byakugan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.services.StorageService
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val BYAKUGAN_TAG = "ByakuganActivity"

class ByakuganActivity : ComponentActivity() {

    private val storageService = StorageService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable Material 3 dynamic colors (Android 12+)
        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            ByakuganApp()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ByakuganApp() {
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
            val absolutePath = storageService.getFilePathFromUri(this@ByakuganActivity, uri)
            Log.i(BYAKUGAN_TAG, "Absolute path: $absolutePath")

            if (absolutePath != null) {
                lifecycleScope.launch {
                    Log.i(BYAKUGAN_TAG, "Fetching manga list")
                    // Perform file I/O on IO dispatcher to avoid blocking UI thread
                    val listOfManga = withContext(Dispatchers.IO) {
                        MangaParserService.findMangaFiles(absolutePath, this@ByakuganActivity)
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
            checkAndRequestStoragePermission(storagePermissionLauncher)
        }

        MaterialTheme(
            colorScheme = dynamicColorScheme()
        ) {
            Scaffold(
                topBar = {
                    ByakuganTopBar(
                        onSettingsClick = {
                            // Handle settings button click
                        }
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

    @Composable
    private fun dynamicColorScheme(): ColorScheme {
        return dynamicDarkColorScheme(this)
    }

    private fun checkAndRequestStoragePermission(
        launcher: androidx.activity.compose.ManagedActivityResultLauncher<Intent, androidx.activity.result.ActivityResult>
    ) {
        // Android 11+ requires MANAGE_EXTERNAL_STORAGE
        if (!Environment.isExternalStorageManager()) {
            Log.i(BYAKUGAN_TAG, "Requesting MANAGE_EXTERNAL_STORAGE permission")
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = "package:$packageName".toUri()
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ByakuganTopBar(onSettingsClick: () -> Unit) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Library",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun MangaGrid(mangaList: List<MangaMetadataModel>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mangaList) { manga ->
                MangaCard(manga = manga)
            }
        }
    }

    @Composable
    fun MangaCard(manga: MangaMetadataModel) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Image with 3:4 aspect ratio
                AsyncImage(
                    model = manga.coverImagePath?.let { File(it) },
                    contentDescription = manga.filename,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f),
                    contentScale = ContentScale.Crop,
                    placeholder = rememberAsyncImagePainter(R.drawable.placeholder_image_5),
                    error = rememberAsyncImagePainter(R.drawable.placeholder_image_2)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Image name
                Text(
                    text = manga.filename,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Image size (page count)
                Text(
                    text = "${manga.pageCount} pages",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun EmptyState(onOpenFolderClick: () -> Unit) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "404",
                fontSize = 54.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No files found",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select a folder to get started",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onOpenFolderClick) {
                Text(text = "Open Folder")
            }
        }
    }
}
