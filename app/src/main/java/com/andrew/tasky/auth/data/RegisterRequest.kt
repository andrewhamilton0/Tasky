package com.andrew.tasky.auth.data

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
