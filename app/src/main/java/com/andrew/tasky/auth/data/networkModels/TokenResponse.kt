package com.andrew.tasky.auth.data.networkModels

data class TokenResponse(
    val token: String,
    val userId: String,
    val fullName: String
)
