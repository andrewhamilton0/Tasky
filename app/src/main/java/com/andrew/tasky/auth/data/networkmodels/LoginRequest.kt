package com.andrew.tasky.auth.data.networkmodels

data class LoginRequest(
    val email: String,
    val password: String
)
