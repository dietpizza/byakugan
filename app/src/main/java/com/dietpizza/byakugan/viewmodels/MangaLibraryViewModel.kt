package com.dietpizza.byakugan.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dietpizza.byakugan.database.AppDatabase
import com.dietpizza.byakugan.database.MangaMetadataDao
import com.dietpizza.byakugan.models.MangaMetadataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MangaLibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val mangaDao: MangaMetadataDao = database.mangaMetadataDao()

    val allManga: Flow<List<MangaMetadataModel>> = mangaDao.getAllManga()

    fun insertManga(manga: MangaMetadataModel) {
        viewModelScope.launch {
            mangaDao.insertManga(manga)
        }
    }

    fun insertAllManga(mangaList: List<MangaMetadataModel>) {
        viewModelScope.launch {
            mangaDao.insertAllManga(mangaList)
        }
    }

    fun updateManga(manga: MangaMetadataModel) {
        viewModelScope.launch {
            mangaDao.updateManga(manga)
        }
    }

    fun deleteManga(manga: MangaMetadataModel) {
        viewModelScope.launch {
            mangaDao.deleteManga(manga)
        }
    }

    fun deleteAllManga() {
        viewModelScope.launch {
            mangaDao.deleteAllManga()
        }
    }

    fun updateLastPage(filename: String, lastPage: Int) {
        viewModelScope.launch {
            mangaDao.updateLastPage(filename, lastPage)
        }
    }

    suspend fun getMangaByFilename(filename: String): MangaMetadataModel? {
        return mangaDao.getMangaByFilename(filename)
    }
}
