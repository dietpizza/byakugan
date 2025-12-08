package com.dietpizza.byakugan.components.reader

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.runtime.Composable
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.MangaPanelModel

enum class ReaderModes {
    HORIZONTAL,
    VERTICAL,
    WEBTOON
}

@Composable
fun SideToSide(
    pagerState: PagerState,
    manga: MangaMetadataModel,
    panels: List<MangaPanelModel>,
    onPanelScaleChange: (Float) -> Unit
) {
    HorizontalPager(pagerState, beyondViewportPageCount = 2) { page ->
        MangaPanel(manga, panels[page], onScaleChange = onPanelScaleChange)
    }
}

@Composable
fun TopToBottom(
    pagerState: PagerState,
    manga: MangaMetadataModel,
    panels: List<MangaPanelModel>,
    onPanelScaleChange: (Float) -> Unit
) {
    VerticalPager(pagerState, beyondViewportPageCount = 2) { page ->
        MangaPanel(manga, panels[page], onScaleChange = onPanelScaleChange)
    }
}

@Composable
fun Webtoon(
    pagerState: PagerState,
    manga: MangaMetadataModel,
    panels: List<MangaPanelModel>
) {
    VerticalPager(pagerState, beyondViewportPageCount = 2) { page ->
        MangaPanel(manga, panels[page]) {}
    }
}
