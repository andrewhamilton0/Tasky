package com.andrew.tasky.auth

data class LoginRequest(
    val email: String,
    val password: String
)
