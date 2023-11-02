package com.andrew.tasky.auth.data.networkModels

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)
