package com.dietpizza.byakugan.services

import android.content.Context
import android.util.Log
import com.dietpizza.byakugan.viewmodels.InsertResult
import com.dietpizza.byakugan.viewmodels.MangaLibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "MangaLibraryService"

object MangaLibraryService {

    /**
     * Scans a folder for manga files and updates the database
     * @param folderPath Absolute path to the folder containing manga files
     * @param context Application context
     * @param viewModel MangaLibraryViewModel to update the database
     * @param onComplete Callback invoked when scanning is complete with insert results
     */
    suspend fun scanFolderAndUpdateDatabase(
        folderPath: String,
        context: Context,
        viewModel: MangaLibraryViewModel,
        onComplete: ((InsertResult) -> Unit)? = null,
        onProgress: ((progress: Float) -> Unit)? = null
    ) {
        try {
            // Verify folder exists
            val folder = File(folderPath)
            if (!folder.exists() || !folder.isDirectory) {
                Log.e(TAG, "Invalid folder path: $folderPath")
                onComplete?.invoke(InsertResult(0, 0, 0, 0))
                return
            }

            Log.i(TAG, "Scanning folder for manga files: $folderPath")

            // Perform file I/O on IO dispatcher to avoid blocking UI thread
            val listOfManga = withContext(Dispatchers.IO) {
                MangaParserService.findMangaFiles(folderPath, context, onProgress)
            }

            Log.i(TAG, "Found ${listOfManga.size} manga files in folder")

            // Insert manga into database - UI will update reactively
            viewModel.insertAllManga(listOfManga) { result ->
                onComplete?.invoke(result)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning folder: $folderPath", e)
            onComplete?.invoke(InsertResult(0, 0, 0, 0))
        }
    }

    /**
     * Saves the manga folder path to preferences
     * @param context Application context
     * @param folderPath Absolute path to save
     */
    fun saveMangaFolderPath(context: Context, folderPath: String) {
        PreferencesManager.getInstance(context).setMangaFolderPath(folderPath)
        Log.i(TAG, "Saved manga folder path: $folderPath")
    }

    /**
     * Gets the saved manga folder path from preferences
     * @param context Application context
     * @return Saved folder path or null if not set
     */
    fun getSavedMangaFolderPath(context: Context): String? {
        return PreferencesManager.getInstance(context).getMangaFolderPath()
    }

    /**
     * Clears the saved manga folder path from preferences
     * @param context Application context
     */
    fun clearMangaFolderPath(context: Context) {
        PreferencesManager.getInstance(context).clearMangaFolderPath()
        Log.i(TAG, "Cleared manga folder path")
    }
}
