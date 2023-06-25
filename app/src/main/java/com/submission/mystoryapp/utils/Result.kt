package com.submission.mystoryapp.utils

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()

    object Loading: Result<Nothing>()

    data class Error(val error: String) : Result<Nothing>()
}