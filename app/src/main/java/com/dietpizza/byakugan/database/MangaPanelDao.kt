package com.dietpizza.byakugan.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dietpizza.byakugan.models.MangaPanelModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaPanelDao {
    @Query("SELECT * FROM manga_panels")
    fun getAllPanels(): Flow<List<MangaPanelModel>>

    @Query("SELECT * FROM manga_panels WHERE mangaId = :mangaId")
    fun getPanelsForManga(mangaId: String): Flow<List<MangaPanelModel>>

    @Query("SELECT * FROM manga_panels WHERE mangaId = :mangaId")
    suspend fun getPanelsForMangaSuspend(mangaId: String): List<MangaPanelModel>

    @Query("SELECT * FROM manga_panels WHERE id = :id")
    suspend fun getPanelById(id: String): MangaPanelModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPanel(panel: MangaPanelModel): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPanels(panels: List<MangaPanelModel>): List<Long>

    @Update
    suspend fun updatePanel(panel: MangaPanelModel)

    @Delete
    suspend fun deletePanel(panel: MangaPanelModel)

    @Query("DELETE FROM manga_panels WHERE mangaId = :mangaId")
    suspend fun deletePanelsForManga(mangaId: String)

    @Query("DELETE FROM manga_panels")
    suspend fun deleteAllPanels()

    @Query("SELECT COUNT(*) FROM manga_panels WHERE mangaId = :mangaId")
    suspend fun getPanelCountForManga(mangaId: String): Int
}
