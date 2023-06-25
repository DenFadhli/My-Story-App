package com.submission.mystoryapp.data.network

import com.submission.mystoryapp.data.local.entity.StoryEntity

data class SignupResponse(
    val message: String,
    val error: Boolean
)

data class LoginResponse(
    val loginResult: LoginResultResponse? = null,
    val message: String? = null,
    val error: Boolean? = null
)

data class LoginResultResponse(
    val name: String? = null,
    val userId: String? = null,
    val token: String? = null
)

data class StoryResponse(
    val error: Boolean?,
    val message: String?,
    val listStory: List<StoryEntity>
)

data class StoryUploadResponse(
    val error: Boolean? = null,
    val message: String? = null
)