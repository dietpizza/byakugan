package com.dietpizza.byakugan.components.reader

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ReaderScreen"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReaderScreen(
    context: Context,
    colorScheme: ColorScheme,
    lifecycleScope: CoroutineScope,
    mangaId: String,
    mangaLibraryViewmodel: MangaLibraryViewModel,
    mangaPanelViewmodel: MangaPanelViewModel
) {
    val mangaPanels by mangaPanelViewmodel.getPanelsForManga(mangaId).collectAsState(initial = null)
    val manga by mangaLibraryViewmodel.getMangaById(mangaId).collectAsState(initial = null)

    var parsingProgress by remember { mutableFloatStateOf(0f) }
    var isParsing by remember { mutableStateOf(false) }

    LaunchedEffect(manga) {
        manga?.let {
            lifecycleScope.launch {
                delay(200)
                isParsing = true
                val panels =
                    withContext(Dispatchers.IO) {
                        MangaParserService(it.path, context)
                            .getPanelsMetadata(it.id) { progress ->
                                parsingProgress = progress
                                Log.e(TAG, "Progress Update: ${progress / 100}")
                            }
                    }
                delay(1000)
                isParsing = false

                Log.e(TAG, "Manga Panels $panels")
            }

        }
    }

    val onSettingsClick: () -> Unit = {

    }


    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isParsing)
                    CustomLoadingDialog(title = manga?.title ?: "", progress = parsingProgress)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomLoadingDialog(title: String, progress: Float) {

    BasicAlertDialog(
        content = {
            Card {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularWavyProgressIndicator(
                        progress = { progress / 100 }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Loading: $title",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        maxLines = 2
                    )
                }
            }
        },
        onDismissRequest = {},
    )
}

