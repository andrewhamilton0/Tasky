package com.andrew.tasky.core.data.util

sealed interface ApiErrorType {
    object IOException : ApiErrorType
    object HttpException : ApiErrorType
    object UnknownError : ApiErrorType
    object Unauthorized : ApiErrorType
}
