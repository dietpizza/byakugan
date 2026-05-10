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

    fun getPanelsForManga(mangaId: String): Flow<List<MangaPanelModel>> {
        return panelDao.getPanelsForManga(mangaId)
    }

    fun insertPanels(panels: List<MangaPanelModel>, onComplete: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val rowIds = panelDao.insertPanels(panels)
                val insertedCount = rowIds.count { it != -1L }
                Log.i(
                    TAG,
                    "Batch insert complete - Total: ${panels.size}, Inserted: $insertedCount"
                )
                onComplete?.invoke(insertedCount)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to insert panels", e)
                onComplete?.invoke(0)
            }
        }
    }

}
