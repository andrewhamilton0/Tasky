package com.andrew.tasky.core.data

import com.andrew.tasky.agenda.data.AgendaApi
import com.andrew.tasky.auth.data.AuthApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {

    val agendaApi: AgendaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AgendaApi::class.java)
    }

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://tasky.pl-coding.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
