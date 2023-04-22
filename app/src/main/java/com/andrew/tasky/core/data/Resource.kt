package com.andrew.tasky.core.data

import com.andrew.tasky.core.UiText
import com.andrew.tasky.core.data.util.ApiErrorType

sealed class Resource<T>(
    val data: T? = null,
    val message: UiText? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data = data)
    class Error<T>(
        errorMessage: UiText? = null,
        val errorType: ApiErrorType = ApiErrorType.UnknownError
    ) : Resource<T>(message = errorMessage)
}
