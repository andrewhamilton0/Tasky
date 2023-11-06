package com.andrew.tasky.auth.data.networkmodels

data class TokenResponse(
    val token: String,
    val userId: String,
    val fullName: String
)
