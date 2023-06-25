package com.submission.mystoryapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.submission.mystoryapp.data.network.UserStory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE] = false
            preferences.remove(STATE)
            preferences.remove(TOKEN)
        }
    }

    fun getAuthToken(): Flow<UserStory> {
        return dataStore.data.map { preferences ->
            UserStory(
                preferences[TOKEN] ?: "",
                preferences[NAME] ?: "",
                preferences[STATE] ?: false
            )
        }
    }

    suspend fun saveAuthToken(user: UserStory) {
        dataStore.edit { preferences ->
            preferences[NAME] = user.name
            preferences[TOKEN] = user.token
            preferences[STATE] = user.isLogin
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreference? = null
        private val NAME = stringPreferencesKey("name")
        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}