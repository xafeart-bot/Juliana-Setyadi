package com.example.argon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedEbookEntity::class,
        PodcastProgressEntity::class,
        LocalBookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ArgonLocalDatabase : RoomDatabase() {

    abstract fun cachedEbookDao(): CachedEbookDao
    abstract fun podcastProgressDao(): PodcastProgressDao
    abstract fun localBookmarkDao(): LocalBookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: ArgonLocalDatabase? = null

        fun getInstance(context: Context): ArgonLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArgonLocalDatabase::class.java,
                    "argon_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
