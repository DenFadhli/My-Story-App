package com.submission.mystoryapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.submission.mystoryapp.data.datastore.AuthPreference
import com.submission.mystoryapp.data.local.db.StoryDatabase
import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.data.network.ApiService
import com.submission.mystoryapp.data.network.LoginRequest
import com.submission.mystoryapp.data.network.LoginResponse
import com.submission.mystoryapp.data.network.SignupRequest
import com.submission.mystoryapp.data.network.SignupResponse
import com.submission.mystoryapp.data.network.StoryResponse
import com.submission.mystoryapp.data.network.StoryUploadResponse
import com.submission.mystoryapp.data.network.UserStory
import com.submission.mystoryapp.data.remote.StoryRemoteMediator
import com.submission.mystoryapp.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

@OptIn(ExperimentalPagingApi::class)
class StoryRepository(private val apiService: ApiService, private val authPreference: AuthPreference, private val storyDatabase: StoryDatabase
) {
    fun onLogin(email: String, password: String) : LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.userLogin(LoginRequest(email, password))
            emit(Result.Success(response))
        } catch (exception: Exception) {
            Log.d("onLogin: ", exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun onSignup(name: String, email: String, password: String) : LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.userSignup(SignupRequest(name, email, password))
            emit(Result.Success(response))
        } catch (exception: Exception) {
            Log.d("UserSignup", exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun getAllStories(): LiveData<PagingData<StoryEntity>> {
        val pagingSourceFactory = { storyDatabase.StoryDao().getAllStory() }
        val remoteMediator = StoryRemoteMediator(apiService, storyDatabase, authPreference)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).liveData
    }

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody) :
            LiveData<Result<StoryUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.createStory(token, file, description, lat, lon)
            emit(Result.Success(response))
        } catch (exception: Exception) {
            Log.d("uploadStory", exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    fun getStoriesLocation(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesLocation(token, 1)
            emit(Result.Success(response))
        } catch (exception: Exception) {
            Log.d("Signup", exception.message.toString())
            emit(Result.Error(exception.message.toString()))
        }
    }

    suspend fun login() {
        authPreference.login()
    }

    suspend fun logout() {
        authPreference.logout()
    }

    suspend fun saveUser(user: UserStory) {
        authPreference.saveAuthToken(user)
    }

    fun getUser(): LiveData<UserStory> {
        return authPreference.getAuthToken().asLiveData()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, authPreference: AuthPreference, storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, authPreference, storyDatabase)
            }.also {
                instance = it
            }
    }
}