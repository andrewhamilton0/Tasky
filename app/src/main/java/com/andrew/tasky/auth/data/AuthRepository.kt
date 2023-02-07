package com.andrew.tasky.auth.data

interface AuthRepository {
    suspend fun register(fullName: String, email: String, password: String): AuthResult<Unit>
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
}
