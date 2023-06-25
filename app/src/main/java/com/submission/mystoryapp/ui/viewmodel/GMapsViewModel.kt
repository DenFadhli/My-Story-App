package com.submission.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.submission.mystoryapp.data.network.UserStory
import com.submission.mystoryapp.data.repository.StoryRepository

class GMapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<UserStory> {
        return storyRepository.getUser()
    }

    fun getStoriesLocation(token: String) = storyRepository.getStoriesLocation(token)
}