package com.submission.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.data.network.UserStory
import com.submission.mystoryapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<UserStory> {
        return storyRepository.getUser()
    }

    fun uploadStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody, lon: RequestBody) =
        storyRepository.uploadStory(token, file, description, lat, lon)

    fun getStories(): LiveData<PagingData<StoryEntity>> {
        return storyRepository.getAllStories().cachedIn(viewModelScope)
    }
}