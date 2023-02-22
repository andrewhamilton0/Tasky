package com.andrew.tasky.auth.util

import com.andrew.tasky.R
import com.andrew.tasky.core.ErrorMessageDto
import com.andrew.tasky.core.Resource
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response

suspend fun <T> getResourceResult(apiToBeCalled: suspend () -> Response<T>): Resource<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiToBeCalled()
            if (response.isSuccessful) {
                Resource.Success(data = response.body()!!)
            } else {
                val errorResponse: ErrorMessageDto? = convertErrorBody(response.errorBody())
                Resource.Error(
                    errorMessage = errorResponse?.message ?: R.string.unknown_error.toString()
                )
            }
        } catch (e: HttpException) {
            Resource.Error(
                errorMessage = e.message ?: R.string.unknown_error.toString()
            )
        } catch (e: IOException) {
            Resource.Error(R.string.please_check_your_internet_connection.toString())
        } catch (e: Exception) {
            Resource.Error(R.string.unknown_error.toString())
        }
    }
}

private fun convertErrorBody(errorBody: ResponseBody?): ErrorMessageDto? {
    return try {
        errorBody?.source()?.let {
            val moshiAdapter = Moshi.Builder().build().adapter(ErrorMessageDto::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}
