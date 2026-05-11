package com.dietpizza.byakugan.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.dietpizza.byakugan.database.AppDatabase
import com.dietpizza.byakugan.database.MangaMetadataDao
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.SortSettings
import com.dietpizza.byakugan.services.PreferencesManager
import com.dietpizza.byakugan.utils.InsertResult
import com.dietpizza.byakugan.utils.createCatastrophicFailureResult
import com.dietpizza.byakugan.utils.deleteMangaByPathsNotInSafe
import com.dietpizza.byakugan.utils.getExistingFilenames
import com.dietpizza.byakugan.utils.getMangaFlowBySortSettings
import com.dietpizza.byakugan.utils.insertMangaBatch
import com.dietpizza.byakugan.utils.logBatchInsertResult
import com.dietpizza.byakugan.utils.separateNewManga
import com.dietpizza.byakugan.utils.updateLastPageSafe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

private const val TAG = "MangaLibraryViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class MangaLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val mangaDao: MangaMetadataDao = database.mangaMetadataDao()
    private val preferencesManager: PreferencesManager = PreferencesManager.getInstance(application)

    // Sort settings state
    private val _sortSettings = MutableStateFlow(preferencesManager.getSortSettings())
    val sortSettings = _sortSettings

    private val refreshTrigger = MutableStateFlow(0)

    // Reactive manga list based on sort settings. Combine with refreshTrigger to allow forced updates.
    val allManga: Flow<List<MangaMetadataModel>> =
        combine(_sortSettings, refreshTrigger) { settings, _ -> settings }
            .flatMapLatest { settings -> getMangaFlowBySortSettings(mangaDao, settings) }

    fun forceRefresh() {
        refreshTrigger.value += 1
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
                logBatchInsertResult(result)
                onComplete?.invoke(result)
            } catch (e: Exception) {
                Log.e(TAG, "Batch insert failed catastrophically", e)
                onComplete?.invoke(createCatastrophicFailureResult(mangaList.size))
            }
        }
    }

    private suspend fun insertAllMangaSafe(mangaList: List<MangaMetadataModel>): InsertResult {
        if (mangaList.isEmpty()) {
            return InsertResult(0, 0, 0, 0)
        }

        return database.withTransaction {
            val totalCount = mangaList.size

            // Step 1: Get existing filenames from database
            val existingFilenames = getExistingFilenames(mangaDao, mangaList.map { it.path })

            // Step 2: Separate new and existing manga
            val (newManga, skippedCount) = separateNewManga(mangaList, existingFilenames)

            // Step 3: Perform batch insertion with fallback
            val (insertedCount, failedCount) = insertMangaBatch(mangaDao, newManga)


            InsertResult(totalCount, insertedCount, skippedCount, failedCount)
        }
    }

    fun updateLastPage(id: String, lastPage: Int) {
        viewModelScope.launch {
            updateLastPageSafe(database, id, lastPage)
        }
    }

    fun deleteMangaByPathsNotIn(filePaths: Set<String>, onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            val deletedCount = deleteMangaByPathsNotInSafe(mangaDao, filePaths)
            onComplete?.invoke(deletedCount)
        }
    }

    fun getMangaById(id: String): Flow<MangaMetadataModel?> {
        return mangaDao.getMangaById(id)
    }
}
