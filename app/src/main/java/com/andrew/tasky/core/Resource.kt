package com.andrew.tasky.core

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data = data)
    class Error<T>(errorMessage: String? = null) : Resource<T>(message = errorMessage)
}
