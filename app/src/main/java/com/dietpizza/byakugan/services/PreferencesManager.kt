package com.dietpizza.byakugan.services

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "byakugan_preferences"
        private const val KEY_MANGA_FOLDER_PATH = "manga_folder_path"

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
}
