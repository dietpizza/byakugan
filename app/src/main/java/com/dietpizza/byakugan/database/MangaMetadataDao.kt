package com.dietpizza.byakugan.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dietpizza.byakugan.models.MangaMetadataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaMetadataDao {
    @Query("SELECT * FROM manga_metadata")
    fun getAllManga(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata WHERE filename = :filename")
    suspend fun getMangaByFilename(filename: String): MangaMetadataModel?

    @Query("SELECT filename FROM manga_metadata WHERE filename IN (:filenames)")
    suspend fun getExistingFilenames(filenames: List<String>): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertManga(manga: MangaMetadataModel): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMangaInternal(manga: List<MangaMetadataModel>): List<Long>

    @Update
    suspend fun updateManga(manga: MangaMetadataModel)

    @Delete
    suspend fun deleteManga(manga: MangaMetadataModel)

    @Query("DELETE FROM manga_metadata")
    suspend fun deleteAllManga()

    @Query("UPDATE manga_metadata SET lastPage = :lastPage WHERE filename = :filename")
    suspend fun updateLastPage(filename: String, lastPage: Int)
}
