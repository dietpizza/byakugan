package com.dietpizza.byakugan.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dietpizza.byakugan.database.AppDatabase
import com.dietpizza.byakugan.database.MangaPanelDao
import com.dietpizza.byakugan.models.MangaPanelModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "MangaPanelViewModel"

class MangaPanelViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val panelDao: MangaPanelDao = database.mangaPanelDao()

    val allPanels: Flow<List<MangaPanelModel>> = panelDao.getAllPanels()

    fun getPanelsForManga(mangaId: String): Flow<List<MangaPanelModel>> {
        return panelDao.getPanelsForManga(mangaId)
    }

    fun insertPanel(panel: MangaPanelModel) {
        viewModelScope.launch {
            try {
                val rowId = panelDao.insertPanel(panel)
                if (rowId == -1L) {
                    Log.w(TAG, "Panel already exists: ${panel.id}")
                } else {
                    Log.i(TAG, "Panel inserted: ${panel.id}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert panel: ${panel.id}", e)
            }
        }
    }

    fun insertPanels(panels: List<MangaPanelModel>, onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val rowIds = panelDao.insertPanels(panels)
                val insertedCount = rowIds.count { it != -1L }
                Log.i(TAG, "Batch insert complete - Total: ${panels.size}, Inserted: $insertedCount")
                onComplete?.invoke(insertedCount)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert panels", e)
                onComplete?.invoke(0)
            }
        }
    }

    fun updatePanel(panel: MangaPanelModel) {
        viewModelScope.launch {
            try {
                panelDao.updatePanel(panel)
                Log.i(TAG, "Panel updated: ${panel.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update panel: ${panel.id}", e)
            }
        }
    }

    fun deletePanel(panel: MangaPanelModel) {
        viewModelScope.launch {
            try {
                panelDao.deletePanel(panel)
                Log.i(TAG, "Panel deleted: ${panel.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete panel: ${panel.id}", e)
            }
        }
    }

    fun deletePanelsForManga(mangaId: String) {
        viewModelScope.launch {
            try {
                panelDao.deletePanelsForManga(mangaId)
                Log.i(TAG, "All panels deleted for manga: $mangaId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete panels for manga: $mangaId", e)
            }
        }
    }

    fun deleteAllPanels() {
        viewModelScope.launch {
            try {
                panelDao.deleteAllPanels()
                Log.i(TAG, "All panels deleted")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete all panels", e)
            }
        }
    }

    suspend fun getPanelById(id: String): MangaPanelModel? {
        return try {
            panelDao.getPanelById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get panel by id: $id", e)
            null
        }
    }

    suspend fun getPanelsForMangaSuspend(mangaId: String): List<MangaPanelModel> {
        return try {
            panelDao.getPanelsForMangaSuspend(mangaId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get panels for manga: $mangaId", e)
            emptyList()
        }
    }

    suspend fun getPanelCountForManga(mangaId: String): Int {
        return try {
            panelDao.getPanelCountForManga(mangaId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get panel count for manga: $mangaId", e)
            0
        }
    }
}
