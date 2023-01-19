package com.andrew.tasky.auth.util

import com.andrew.tasky.auth.data.AuthResult
import retrofit2.HttpException

inline fun <T> getAuthResult(run: () -> T): AuthResult<T> {
    return try {
        val response = run()
        AuthResult.Authorized(response)
    } catch (e: HttpException) {
        if (e.code() == 401) {
            AuthResult.Unauthorized()
        } else {
            AuthResult.UnknownError()
        }
    } catch (e: Exception) {
        AuthResult.UnknownError()
    }
}
