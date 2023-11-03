package com.andrew.tasky.auth.data.networkmodels

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
