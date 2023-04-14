package com.andrew.tasky.core.data

import com.andrew.tasky.core.UiText

sealed class Resource<T>(
    val data: T? = null,
    val message: UiText? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data = data)
    class Error<T>(errorMessage: UiText? = null) : Resource<T>(message = errorMessage)
}
