package com.dietpizza.byakugan.services

import android.content.Context
import android.util.Log
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.viewmodels.MangaPanelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "MangaPanelService"


object MangaPanelService {
    suspend fun parseMangaPanels(
        manga: MangaMetadataModel,
        context: Context,
        viewModel: MangaPanelViewModel,
        onComplete: (() -> Unit)? = null,
        onProgress: ((progress: Float) -> Unit)? = null
    ) {
        try {

            val file = File(manga.path)

            if (!file.exists()) {
                Log.e(TAG, "Invalid file path: ${manga.path}")
                onComplete?.invoke()
            }

            val panels =
                withContext(Dispatchers.IO) {
                    MangaParserService(manga.path, context)
                        .getPanelsMetadata(manga.id, onProgress)
                }

            if (panels.isNotEmpty()) {
                viewModel.insertPanels(panels) {
                    onComplete?.invoke()
                }
            } else {
                onComplete?.invoke()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing file: ${manga.path}", e)
            onComplete?.invoke()
        }
    }
}