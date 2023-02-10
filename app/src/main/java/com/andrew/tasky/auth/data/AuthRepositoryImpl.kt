package com.andrew.tasky.auth.data

import android.content.SharedPreferences
import androidx.work.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.auth.util.getAuthResult
import com.andrew.tasky.core.WorkerParamKeys
import com.andrew.tasky.core.data.PrefsKeys

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: SharedPreferences,
    private val agendaRepository: AgendaRepository,
    private val workManager: WorkManager
) : AuthRepository {

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): AuthResult<Unit> {
        val result = getAuthResult {
            api.register(
                request = RegisterRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )
            login(email, password)
        }
        return when (result) {
            is AuthResult.Authorized -> AuthResult.Authorized()
            is AuthResult.Unauthorized -> AuthResult.Unauthorized()
            is AuthResult.UnknownError -> AuthResult.UnknownError()
        }
    }

    override suspend fun login(email: String, password: String): AuthResult<Unit> {
        val result = getAuthResult {
            val response = api.login(
                request = LoginRequest(
                    email = email,
                    password = password
                )
            )
            prefs.edit()
                .putString(PrefsKeys.JWT, response.token)
                .putString(PrefsKeys.USER_ID, response.userId)
                .putString(PrefsKeys.FULL_NAME, response.fullName)
                .apply()
        }
        return when (result) {
            is AuthResult.Authorized -> AuthResult.Authorized()
            is AuthResult.Unauthorized -> AuthResult.Unauthorized()
            is AuthResult.UnknownError -> AuthResult.UnknownError()
        }
    }

    override suspend fun authenticate(): AuthResult<Unit> {
        val result = getAuthResult {
            val token = prefs.getString(
                PrefsKeys.JWT, null
            ) ?: return AuthResult.Unauthorized()
            api.authenticate("Bearer $token")
        }
        return when (result) {
            is AuthResult.Authorized -> AuthResult.Authorized()
            is AuthResult.Unauthorized -> AuthResult.Unauthorized()
            is AuthResult.UnknownError -> AuthResult.UnknownError()
        }
    }

    override suspend fun logout() {
        val token = prefs.getString(
            PrefsKeys.JWT, null
        )
        val data = Data.Builder().putString(
            WorkerParamKeys.TOKEN,
            token
        ).build()

        val logoutWorker = OneTimeWorkRequestBuilder<LogoutWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()
        workManager.enqueue(logoutWorker)
        prefs.edit().clear().apply()
        agendaRepository.deleteAllAgendaTables()
    }
}
