package com.andrew.tasky.core.data

import com.andrew.tasky.core.domain.SharedPrefs
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val prefs: SharedPrefs
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = prefs.getJwt()

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
