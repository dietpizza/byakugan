package com.dietpizza.byakugan.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dietpizza.byakugan.models.MangaMetadataModel

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
