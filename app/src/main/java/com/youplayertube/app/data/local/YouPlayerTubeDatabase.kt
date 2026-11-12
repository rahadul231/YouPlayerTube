package com.youplayertube.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MediaEntity::class, PlaylistEntity::class, PlaylistEntryEntity::class,
    FavoriteEntity::class, WatchLaterEntity::class, HistoryEntity::class], version = 1, exportSchema = true)
abstract class YouPlayerTubeDatabase : RoomDatabase() {
    abstract fun library(): LibraryDao
    companion object {
        const val NAME = "youplayertube.db"
        fun open(context: Context, name: String = NAME) = Room.databaseBuilder(
            context.applicationContext, YouPlayerTubeDatabase::class.java, name).openHelperFactory(PreservingOpenHelperFactory()).build()
    }
}
