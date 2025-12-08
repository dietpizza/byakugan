package com.dietpizza.byakugan.components.reader

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dietpizza.byakugan.services.MangaPanelService
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "ReaderScreen"

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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
    var mode by remember { mutableStateOf<ReaderModes>(ReaderModes.HORIZONTAL) }
    var currentPage by remember { mutableIntStateOf(manga?.lastPage ?: 0) }
    var panelScale by remember { mutableFloatStateOf(1f) }

    val pagerState = rememberPagerState(
        initialPage = manga?.lastPage ?: 0,
        pageCount = { panels?.size ?: 0 }
    )

    val coroutineScope = rememberCoroutineScope()

    val onPanelScaleChange: (Float) -> Unit = { scale ->
        panelScale = scale
        Log.e(TAG, "PanelScale: $scale")
    }

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
        currentPage = pagerState.currentPage
        mangaLibraryViewmodel.updateLastPage(manga!!.id, lastPage = pagerState.currentPage)
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isParsingFile)
                    MangaLoadingDialog(title = manga!!.title, progress = parsingProgress)

                panels?.let {
                    when (mode) {
                        ReaderModes.HORIZONTAL -> SideToSide(
                            pagerState,
                            manga!!,
                            it,
                            onPanelScaleChange
                        )

                        ReaderModes.VERTICAL -> TopToBottom(
                            pagerState,
                            manga!!,
                            it,
                            onPanelScaleChange
                        )

                        ReaderModes.WEBTOON -> Webtoon(pagerState, manga!!, it)
                    }
                }

                panels?.let {
                    FloatingBottomToolbar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        pageCount = manga!!.pageCount,
                        currentPage = currentPage,
                        panelScale = panelScale,
                        onPageChange = { page ->
                            coroutineScope.launch {
                                pagerState.scrollToPage(page)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FloatingBottomToolbar(
    pageCount: Int,
    currentPage: Int,
    panelScale: Float,
    modifier: Modifier = Modifier,
    onPageChange: (Int) -> Unit,
) {
    var value by remember { mutableIntStateOf(0) }

    val slideAnimationOffsetSpec: (IntSize) -> IntOffset = { size ->
        IntOffset(
            x = 0,
            y = (size.height * 1.5).toInt()
        )
    }

    AnimatedVisibility(
        visible = panelScale < 1.1f,
        modifier = modifier,
        enter = slideIn(initialOffset = slideAnimationOffsetSpec),
        exit = slideOut(targetOffset = slideAnimationOffsetSpec)
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Slider(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                value = currentPage.toFloat(),
                onValueChange = { value = it.toInt() },
                valueRange = 0f..pageCount.toFloat(),
                onValueChangeFinished = { onPageChange(value) }
            )
        }
    }
}