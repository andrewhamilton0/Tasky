package com.andrew.tasky.auth.domain

import com.andrew.tasky.core.Resource

interface AuthRepository {
    suspend fun register(fullName: String, email: String, password: String): Resource<Unit>
    suspend fun login(email: String, password: String): Resource<Unit>
    suspend fun authenticate(): Resource<Unit>
    suspend fun logout()
}
