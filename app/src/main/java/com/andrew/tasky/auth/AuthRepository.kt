package com.andrew.tasky.auth

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): AuthResult<Unit>
    suspend fun login(email: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
}
