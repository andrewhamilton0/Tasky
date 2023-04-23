package com.andrew.tasky.auth.util

import com.andrew.tasky.R
import com.andrew.tasky.core.UiText
import com.andrew.tasky.core.data.ErrorMessageDto
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.core.data.util.ApiErrorType
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
                Resource.Success(data = response.body())
            } else {
                var errorType: ApiErrorType = ApiErrorType.UnknownError
                if (response.code() == 401) errorType = ApiErrorType.Unauthorized

                val errorResponse: ErrorMessageDto? = convertErrorBody(response.errorBody())
                Resource.Error(
                    errorMessage = errorResponse?.message?.let { UiText.DynamicString(value = it) }
                        ?: UiText.Resource(resId = R.string.unknown_error),
                    errorType = errorType
                )
            }
        } catch (e: HttpException) {
            Resource.Error(
                errorMessage = UiText.Resource(resId = R.string.unknown_error),
                errorType = ApiErrorType.HttpException
            )
        } catch (e: IOException) {
            Resource.Error(
                errorMessage = UiText.Resource(
                    resId = R.string.please_check_your_internet_connection
                ),
                errorType = ApiErrorType.IOException
            )
        } catch (e: Exception) {
            Resource.Error(
                errorMessage = UiText.Resource(resId = R.string.unknown_error),
                errorType = ApiErrorType.UnknownError
            )
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
