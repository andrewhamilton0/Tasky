package com.andrew.tasky.auth.data

import com.andrew.tasky.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

object ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request: Request = chain.request()

        request = request.newBuilder()
            .addHeader("x-api-key", BuildConfig.API_KEY)
            .build()

        return chain.proceed(request)
    }
}
