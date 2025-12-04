package com.dietpizza.byakugan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dietpizza.byakugan.models.MangaMetadataModel
import com.dietpizza.byakugan.models.MangaPanelModel

@Database(
    entities = [MangaMetadataModel::class, MangaPanelModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaMetadataDao(): MangaMetadataDao
    abstract fun mangaPanelDao(): MangaPanelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "byakugan_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
