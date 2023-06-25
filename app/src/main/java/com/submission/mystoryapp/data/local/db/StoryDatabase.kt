package com.submission.mystoryapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.submission.mystoryapp.data.local.dao.RemoteKeyDao
import com.submission.mystoryapp.data.local.dao.StoryDao
import com.submission.mystoryapp.data.local.entity.RemoteKeyEntity
import com.submission.mystoryapp.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeyEntity::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun StoryDao(): StoryDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                StoryDatabase::class.java, "story.db"
            ).build()
    }
}