package com.andrew.tasky.auth.data

import com.andrew.tasky.auth.data.networkmodels.LoginRequest
import com.andrew.tasky.auth.data.networkmodels.RegisterRequest
import com.andrew.tasky.auth.data.networkmodels.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Unit>

    @POST("/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    @GET("/authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    ): Response<Unit>

    @GET("/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>
}
