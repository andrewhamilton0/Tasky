package com.andrew.tasky.core.data

import android.content.SharedPreferences
import com.andrew.tasky.auth.util.PrefsKeys
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val prefs: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = prefs.getString(PrefsKeys.JWT, null)

        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
