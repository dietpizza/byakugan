package com.dietpizza.byakugan.utils

import android.util.Log
import com.dietpizza.byakugan.database.AppDatabase
import com.dietpizza.byakugan.database.MangaMetadataDao
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.SortBy
import com.dietpizza.byakugan.models.SortOrder
import com.dietpizza.byakugan.models.SortSettings
import kotlinx.coroutines.flow.Flow

private const val TAG = "MangaLibraryViewModelUtils"

// ============================================================================
// SORTING OPERATIONS
// ============================================================================

/**
 * Retrieves the appropriate manga flow based on sort settings.
 *
 * @param mangaDao DAO for database access
 * @param sortSettings The sort settings to apply
 * @return Flow of sorted manga list
 */
fun getMangaFlowBySortSettings(
    mangaDao: MangaMetadataDao,
    sortSettings: SortSettings
): Flow<List<MangaMetadataModel>> {
    return when (sortSettings.sortBy) {
        SortBy.NAME -> getSortedByName(mangaDao, sortSettings.sortOrder)
        SortBy.PAGES -> getSortedByPages(mangaDao, sortSettings.sortOrder)
        SortBy.TIME -> getSortedByTime(mangaDao, sortSettings.sortOrder)
    }
}

/**
 * Gets manga sorted by name.
 */
private fun getSortedByName(
    mangaDao: MangaMetadataDao,
    sortOrder: SortOrder
): Flow<List<MangaMetadataModel>> {
    return if (sortOrder == SortOrder.ASCENDING) {
        mangaDao.getAllMangaSortedByNameAsc()
    } else {
        mangaDao.getAllMangaSortedByNameDesc()
    }
}

/**
 * Gets manga sorted by page count.
 */
private fun getSortedByPages(
    mangaDao: MangaMetadataDao,
    sortOrder: SortOrder
): Flow<List<MangaMetadataModel>> {
    return if (sortOrder == SortOrder.ASCENDING) {
        mangaDao.getAllMangaSortedByPagesAsc()
    } else {
        mangaDao.getAllMangaSortedByPagesDesc()
    }
}

/**
 * Gets manga sorted by timestamp.
 */
private fun getSortedByTime(
    mangaDao: MangaMetadataDao,
    sortOrder: SortOrder
): Flow<List<MangaMetadataModel>> {
    return if (sortOrder == SortOrder.ASCENDING) {
        mangaDao.getAllMangaSortedByTimeAsc()
    } else {
        mangaDao.getAllMangaSortedByTimeDesc()
    }
}

// ============================================================================
// INSERTION OPERATIONS
// ============================================================================

/**
 * Retrieves the set of existing manga filenames from the database.
 *
 * @param mangaDao DAO for database access
 * @param filePaths List of file paths to check
 * @return Set of existing filenames, or empty set if query fails
 */
suspend fun getExistingFilenames(
    mangaDao: MangaMetadataDao,
    filePaths: List<String>
): Set<String> {
    return try {
        mangaDao.getExistingFilenames(filePaths).toSet()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to query existing filenames", e)
        emptySet()
    }
}

/**
 * Filters manga list to separate new and existing items.
 *
 * @param mangaList The full list of manga to process
 * @param existingFilenames Set of paths that already exist in the database
 * @return Pair of (newMangaList, skippedCount)
 */
suspend fun separateNewManga(
    mangaList: List<MangaMetadataModel>,
    existingFilenames: Set<String>
): Pair<List<MangaMetadataModel>, Int> {
    val newManga = mutableListOf<MangaMetadataModel>()
    var skippedCount = 0

    for (manga in mangaList) {
        if (manga.path in existingFilenames) {
            skippedCount++
        } else {
            newManga.add(manga)
        }
    }

    return newManga to skippedCount
}

/**
 * Attempts batch insertion of manga.
 * Falls back to individual inserts if batch insert fails.
 *
 * @param mangaDao DAO for database access
 * @param newManga List of new manga to insert
 * @return Pair of (insertedCount, failedCount)
 */
suspend fun insertMangaBatch(
    mangaDao: MangaMetadataDao,
    newManga: List<MangaMetadataModel>
): Pair<Int, Int> {
    if (newManga.isEmpty()) {
        return 0 to 0
    }

    return try {
        insertMangaBatchInternal(mangaDao, newManga)
    } catch (e: Exception) {
        Log.e(TAG, "Batch insert failed, attempting individual inserts", e)
        insertMangaIndividually(mangaDao, newManga)
    }
}

/**
 * Performs the actual batch insertion using Room's batch insert method.
 *
 * @param mangaDao DAO for database access
 * @param newManga List of new manga to insert
 * @return Pair of (insertedCount, failedCount)
 */
private suspend fun insertMangaBatchInternal(
    mangaDao: MangaMetadataDao,
    newManga: List<MangaMetadataModel>
): Pair<Int, Int> {
    val insertResults = mangaDao.insertMangaInternal(newManga)
    // Count successful inserts (-1 means conflict/skipped)
    val insertedCount = insertResults.count { it != -1L }
    val failedCount = insertResults.count { it == -1L }
    return insertedCount to failedCount
}

/**
 * Inserts manga one by one as a fallback mechanism.
 *
 * @param mangaDao DAO for database access
 * @param newManga List of new manga to insert
 * @return Pair of (insertedCount, failedCount)
 */
private suspend fun insertMangaIndividually(
    mangaDao: MangaMetadataDao,
    newManga: List<MangaMetadataModel>
): Pair<Int, Int> {
    var insertedCount = 0
    var failedCount = 0

    for (manga in newManga) {
        try {
            val rowId = mangaDao.insertManga(manga)
            if (rowId != -1L) {
                insertedCount++
            } else {
                failedCount++
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to insert manga: ${manga.path}", ex)
            failedCount++
        }
    }

    return insertedCount to failedCount
}

// ============================================================================
// UPDATE/DELETE OPERATIONS
// ============================================================================

/**
 * Updates the last read page for a manga entry.
 *
 * @param mangaDao DAO for database access
 * @param id The manga ID
 * @param lastPage The last page number
 * @return True if update succeeded, False otherwise
 */
suspend fun updateLastPageSafe(
    database: AppDatabase,
    id: String,
    lastPage: Int
): Boolean {
    return try {
        database.openHelper.writableDatabase.execSQL(
            "UPDATE manga_metadata SET lastPage = ? WHERE id = ?",
            arrayOf<Any>(lastPage, id)
        )
        Log.i(TAG, "Last page updated for: $id to page $lastPage")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Failed to update last page for: $id", e)
        false
    }
}

/**
 * Deletes manga records not found in the provided file paths.
 *
 * @param mangaDao DAO for database access
 * @param filePaths Set of file paths to keep
 * @return Number of deleted records, or 0 if operation failed
 */
suspend fun deleteMangaByPathsNotInSafe(
    mangaDao: MangaMetadataDao,
    filePaths: Set<String>
): Int {
    return try {
        val deletedCount = mangaDao.deleteMangaByPathsNotIn(filePaths)
        Log.i(TAG, "Deleted $deletedCount manga records not found in folder")
        deletedCount
    } catch (e: Exception) {
        Log.e(TAG, "Failed to delete manga by paths not in set", e)
        0
    }
}

// ============================================================================
// RESULT & LOGGING OPERATIONS
// ============================================================================

/**
 * Logs batch insert results.
 *
 * @param result The insert result with counts
 */
fun logBatchInsertResult(result: InsertResult) {
    Log.i(
        TAG, "Batch insert complete - Total: ${result.totalCount}, " +
                "Inserted: ${result.insertedCount}, " +
                "Skipped: ${result.skippedCount}, " +
                "Failed: ${result.failedCount}"
    )
}

/**
 * Creates a result for catastrophic insert failure.
 *
 * @param mangaListSize The size of the original manga list
 * @return InsertResult with all items marked as failed
 */
fun createCatastrophicFailureResult(mangaListSize: Int): InsertResult {
    return InsertResult(
        totalCount = mangaListSize,
        insertedCount = 0,
        skippedCount = 0,
        failedCount = mangaListSize
    )
}

