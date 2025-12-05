package com.dietpizza.byakugan.components.reader

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.MangaPanelModel
import com.dietpizza.byakugan.services.ImageCacheService
import com.dietpizza.byakugan.services.MangaParserService
import com.dietpizza.byakugan.zoomable.ExperimentalZoomableApi
import com.dietpizza.byakugan.zoomable.rememberZoomState
import com.dietpizza.byakugan.zoomable.snapBackZoomable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalZoomableApi::class)
@Composable
fun MangaPanel(manga: MangaMetadataModel, panel: MangaPanelModel) {
    val context = LocalContext.current

    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var zoomState = rememberZoomState()

    LaunchedEffect(manga.path, panel.panelName) {
        withContext(Dispatchers.IO) {
            val cached = ImageCacheService.get(panel.id)
            if (cached != null) {
                imageBitmap = cached.asImageBitmap()
                return@withContext
            }

            val parserService = MangaParserService(manga.path, context)
            parserService.getEntryStream(panel.panelName)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let {
                    ImageCacheService.put(panel.id, it)
                    imageBitmap = it.asImageBitmap()
                }
            }
        }
    }

    imageBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = panel.panelName,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .snapBackZoomable(
                    zoomState = zoomState,
                    enableOneFingerZoom = true
                ),
        )
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularWavyProgressIndicator()
    }
}