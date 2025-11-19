package com.dietpizza.byakugan.services

import android.content.Context
import android.content.SharedPreferences
import com.dietpizza.byakugan.models.SortBy
import com.dietpizza.byakugan.models.SortOrder
import com.dietpizza.byakugan.models.SortSettings

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "byakugan_preferences"
        private const val KEY_MANGA_FOLDER_PATH = "manga_folder_path"
        private const val KEY_SORT_BY = "sort_by"
        private const val KEY_SORT_ORDER = "sort_order"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun setMangaFolderPath(path: String) {
        sharedPreferences.edit().putString(KEY_MANGA_FOLDER_PATH, path).apply()
    }

    fun getMangaFolderPath(): String? {
        return sharedPreferences.getString(KEY_MANGA_FOLDER_PATH, null)
    }

    fun clearMangaFolderPath() {
        sharedPreferences.edit().remove(KEY_MANGA_FOLDER_PATH).apply()
    }

    fun setSortSettings(settings: SortSettings) {
        sharedPreferences.edit()
            .putString(KEY_SORT_BY, settings.sortBy.name)
            .putString(KEY_SORT_ORDER, settings.sortOrder.name)
            .apply()
    }

    fun getSortSettings(): SortSettings {
        val sortByString = sharedPreferences.getString(KEY_SORT_BY, SortBy.NAME.name)
        val sortOrderString = sharedPreferences.getString(KEY_SORT_ORDER, SortOrder.ASCENDING.name)

        val sortBy = try {
            SortBy.valueOf(sortByString ?: SortBy.NAME.name)
        } catch (e: IllegalArgumentException) {
            SortBy.NAME
        }

        val sortOrder = try {
            SortOrder.valueOf(sortOrderString ?: SortOrder.ASCENDING.name)
        } catch (e: IllegalArgumentException) {
            SortOrder.ASCENDING
        }

        return SortSettings(sortBy, sortOrder)
    }
}
