package com.submission.mystoryapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.submission.mystoryapp.data.datastore.AuthPreference
import com.submission.mystoryapp.data.local.db.StoryDatabase
import com.submission.mystoryapp.data.network.ApiConfig
import com.submission.mystoryapp.data.repository.StoryRepository

private const val DATASTORE_NAME = "mystoryapp"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)
object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val storyDatabase = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, authPreference, storyDatabase)
    }
}