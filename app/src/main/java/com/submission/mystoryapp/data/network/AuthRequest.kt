package com.submission.mystoryapp.data.network

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)