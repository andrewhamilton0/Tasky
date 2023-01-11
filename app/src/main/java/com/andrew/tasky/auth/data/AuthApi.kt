package com.andrew.tasky.auth.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("/register")
    suspend fun register(
        @Body request: RegisterRequest
    )

    @POST("/login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @GET("/authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )
}
