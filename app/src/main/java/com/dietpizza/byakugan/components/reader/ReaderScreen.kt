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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dietpizza.byakugan.services.MangaPanelService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val manga by mangaLibraryViewmodel.getMangaById(mangaId)
        .collectAsStateWithLifecycle(initialValue = null)
    val panels by mangaPanelViewmodel.getPanelsForManga(mangaId)
        .collectAsStateWithLifecycle(initialValue = null)

    // Skip composition until manga is loaded
    if (manga == null) return

    var parsingProgress by remember { mutableFloatStateOf(0f) }
    var isParsingFile by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(
        initialPage = manga?.lastPage ?: 0,
        pageCount = { panels?.size ?: 0 }
    )

    LaunchedEffect(panels) {
        if (panels != null && panels!!.isEmpty()) {
            Log.e(TAG, "Getting Manga Panels")
            lifecycleScope.launch {
                isParsingFile = true
                MangaPanelService.parseMangaPanels(
                    manga!!, context, mangaPanelViewmodel,
                    onProgress = { progress ->
                        parsingProgress = progress
                    },
                    onComplete = {
                        isParsingFile = false
                    }
                )
            }
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        Log.e(TAG, "Current Page: ${pagerState.currentPage}")
        mangaLibraryViewmodel.updateLastPage(manga!!.id, lastPage = pagerState.currentPage)
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
                if (isParsingFile)
                    CustomLoadingDialog(title = manga!!.title, progress = parsingProgress)

                panels?.let { panel ->
                    HorizontalPager(pagerState, beyondViewportPageCount = 2) { page ->
                        MangaPanel(manga!!, panel[page])
                    }
                }
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
