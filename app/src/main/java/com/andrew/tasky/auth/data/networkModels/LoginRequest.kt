package com.andrew.tasky.auth.data.networkModels

data class LoginRequest(
    val email: String,
    val password: String
)
