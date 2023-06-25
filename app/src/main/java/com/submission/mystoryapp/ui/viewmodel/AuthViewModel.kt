package com.submission.mystoryapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.mystoryapp.data.network.UserStory
import com.submission.mystoryapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<UserStory> = storyRepository.getUser()

    fun saveUser(user: UserStory) {
        viewModelScope.launch {
            storyRepository.saveUser(user)
        }
    }

    fun userLogin(email: String, password: String) = storyRepository.onLogin(email, password)

    fun userSignup(name: String, email: String, password: String) = storyRepository.onSignup(name, email, password)

    fun login() {
        viewModelScope.launch {
            storyRepository.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }

    fun fetchUser(): LiveData<UserStory> {
        return storyRepository.getUser()
    }
}