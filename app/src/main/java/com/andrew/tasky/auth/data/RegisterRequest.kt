package com.andrew.tasky.auth.data

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
