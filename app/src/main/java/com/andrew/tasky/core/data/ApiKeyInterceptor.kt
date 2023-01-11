package com.andrew.tasky.core.data

import com.andrew.tasky.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

object ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
            .newBuilder()
            .addHeader("x-api-key", BuildConfig.API_KEY)
            .build()

        return chain.proceed(request)
    }
}
