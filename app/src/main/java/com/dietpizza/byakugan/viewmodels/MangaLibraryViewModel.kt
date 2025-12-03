package com.dietpizza.byakugan.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.dietpizza.byakugan.database.AppDatabase
import com.dietpizza.byakugan.database.MangaMetadataDao
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.SortBy
import com.dietpizza.byakugan.models.SortOrder
import com.dietpizza.byakugan.models.SortSettings
import com.dietpizza.byakugan.services.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val TAG = "MangaLibraryViewModel"

data class InsertResult(
    val totalCount: Int,
    val insertedCount: Int,
    val skippedCount: Int,
    val failedCount: Int
)

@OptIn(ExperimentalCoroutinesApi::class)
class MangaLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val mangaDao: MangaMetadataDao = database.mangaMetadataDao()
    private val preferencesManager: PreferencesManager = PreferencesManager.getInstance(application)

    // Sort settings state
    private val _sortSettings = MutableStateFlow(preferencesManager.getSortSettings())
    val sortSettings: Flow<SortSettings> = _sortSettings

    // Reactive manga list based on sort settings. Ignore emissions that only change lastPage to avoid UI updates.
    val allManga: Flow<List<MangaMetadataModel>> = _sortSettings.flatMapLatest { settings ->
        when (settings.sortBy) {
            SortBy.NAME -> when (settings.sortOrder) {
                SortOrder.ASCENDING -> mangaDao.getAllMangaSortedByNameAsc()
                SortOrder.DESCENDING -> mangaDao.getAllMangaSortedByNameDesc()
            }

            SortBy.PAGES -> when (settings.sortOrder) {
                SortOrder.ASCENDING -> mangaDao.getAllMangaSortedByPagesAsc()
                SortOrder.DESCENDING -> mangaDao.getAllMangaSortedByPagesDesc()
            }

            SortBy.TIME -> when (settings.sortOrder) {
                SortOrder.ASCENDING -> mangaDao.getAllMangaSortedByTimeAsc()
                SortOrder.DESCENDING -> mangaDao.getAllMangaSortedByTimeDesc()
            }
        }
    }.distinctUntilChanged { old, new ->
        if (old.size != new.size) return@distinctUntilChanged false
        old.zip(new).all { (a, b) ->
            // Compare all fields except lastPage so updates to lastPage don't trigger UI updates
            a.copy(lastPage = null) == b.copy(lastPage = null)
        }
    }

    fun updateSortSettings(settings: SortSettings) {
        preferencesManager.setSortSettings(settings)
        _sortSettings.value = settings
    }

    fun insertAllManga(
        mangaList: List<MangaMetadataModel>,
        onComplete: ((InsertResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val result = insertAllMangaSafe(mangaList)
                Log.i(
                    TAG, "Batch insert complete - Total: ${result.totalCount}, " +
                            "Inserted: ${result.insertedCount}, " +
                            "Skipped: ${result.skippedCount}, " +
                            "Failed: ${result.failedCount}"
                )
                onComplete?.invoke(result)
            } catch (e: Exception) {
                Log.e(TAG, "Batch insert failed catastrophically", e)
                onComplete?.invoke(
                    InsertResult(
                        totalCount = mangaList.size,
                        insertedCount = 0,
                        skippedCount = 0,
                        failedCount = mangaList.size
                    )
                )
            }
        }
    }

    private suspend fun insertAllMangaSafe(mangaList: List<MangaMetadataModel>): InsertResult {
        if (mangaList.isEmpty()) {
            return InsertResult(0, 0, 0, 0)
        }

        return database.withTransaction {
            val totalCount = mangaList.size
            var insertedCount = 0
            var skippedCount = 0
            var failedCount = 0

            // Check for existing manga to avoid unnecessary insert attempts
            val filenames = mangaList.map { it.path }
            val existingFilenames = try {
                mangaDao.getExistingFilenames(filenames).toSet()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query existing filenames", e)
                emptySet()
            }

            // Filter out duplicates and insert new manga
            val newManga = mangaList.filter { it.path !in existingFilenames }
            skippedCount = mangaList.size - newManga.size

            if (newManga.isNotEmpty()) {
                // Insert in batches to handle potential failures
                for (manga in newManga) {
                    try {
                        val rowId = mangaDao.insertManga(manga)
                        if (rowId != -1L) {
                            insertedCount++
                        } else {
                            // This shouldn't happen since we filtered duplicates, but handle it
                            skippedCount++
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to insert manga: ${manga.path}", e)
                        failedCount++
                    }
                }
            }

            InsertResult(totalCount, insertedCount, skippedCount, failedCount)
        }
    }

    fun updateLastPage(id: String, lastPage: Int) {
        viewModelScope.launch {
            try {
                // Perform direct SQL update to avoid Room invalidation and Flow emissions
                database.openHelper.writableDatabase.execSQL(
                    "UPDATE manga_metadata SET lastPage = ? WHERE id = ?",
                    arrayOf(lastPage, id)
                )
                Log.i(TAG, "Last page silently updated for: $id to page $lastPage")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update last page for: $id", e)
            }
        }
    }

    fun getMangaById(id: String): Flow<MangaMetadataModel?> {
        return mangaDao.getMangaById(id)
    }
}
