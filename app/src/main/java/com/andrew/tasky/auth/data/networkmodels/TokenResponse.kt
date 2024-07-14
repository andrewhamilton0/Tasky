package com.andrew.tasky.auth.data.networkmodels

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val fullName: String,
    val accessTokenExpirationTimestamp: Long
)
