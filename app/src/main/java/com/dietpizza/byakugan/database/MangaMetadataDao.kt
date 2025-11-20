package com.dietpizza.byakugan.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dietpizza.byakugan.models.MangaMetadataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaMetadataDao {
    @Query("SELECT * FROM manga_metadata")
    fun getAllManga(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY path ASC")
    fun getAllMangaSortedByNameAsc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY path DESC")
    fun getAllMangaSortedByNameDesc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY pageCount ASC")
    fun getAllMangaSortedByPagesAsc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY pageCount DESC")
    fun getAllMangaSortedByPagesDesc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY timestamp ASC")
    fun getAllMangaSortedByTimeAsc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata ORDER BY timestamp DESC")
    fun getAllMangaSortedByTimeDesc(): Flow<List<MangaMetadataModel>>

    @Query("SELECT * FROM manga_metadata WHERE id = :id")
    fun getMangaById(id: String): Flow<MangaMetadataModel?>

    @Query("SELECT path FROM manga_metadata WHERE path IN (:filenames)")
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

    @Query("UPDATE manga_metadata SET lastPage = :lastPage WHERE id = :id")
    suspend fun updateLastPage(id: String, lastPage: Int)
}
