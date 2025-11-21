package com.dietpizza.byakugan.components.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dietpizza.byakugan.components.ui.MangaCard
import com.dietpizza.byakugan.models.MangaMetadataModel

@Composable
fun LibraryGrid(
    mangaList: List<MangaMetadataModel>?,
    isRefreshing: Boolean,
    onOpenFolderClick: () -> Unit
) {


    if (mangaList != null) {
        if (mangaList.isEmpty() && !isRefreshing) {
            return LibraryEmpty(onOpenFolderClick)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mangaList) { manga ->
                MangaCard(manga)
            }
        }
    }
}

@Composable
fun LibraryEmpty(onOpenFolderClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Lonely here, it is",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select a folder, you must",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilledTonalButton(onClick = onOpenFolderClick, modifier = Modifier.fillMaxWidth(0.5f)) {
            Text(text = "Select Folder")
        }
    }
}
