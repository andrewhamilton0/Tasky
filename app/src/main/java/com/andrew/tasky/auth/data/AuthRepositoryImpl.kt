package com.andrew.tasky.auth.data

import android.content.SharedPreferences
import androidx.work.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.Resource
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
    ): Resource<Unit> {
        val result = getResourceResult {
            api.register(
                request = RegisterRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )
        }
        return when (result) {
            is Resource.Error -> {
                Resource.Error(result.message)
            }
            is Resource.Success -> {
                login(email, password)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<Unit> {
        val result = getResourceResult {
            api.login(
                request = LoginRequest(
                    email = email,
                    password = password
                )
            )
        }
        return when (result) {
            is Resource.Error -> {
                Resource.Error(result.message)
            }
            is Resource.Success -> {
                prefs.edit()
                    .putString(PrefsKeys.JWT, result.data?.token)
                    .putString(PrefsKeys.USER_ID, result.data?.userId)
                    .putString(PrefsKeys.FULL_NAME, result.data?.fullName)
                    .apply()
                Resource.Success()
            }
        }
    }

    override suspend fun authenticate(): Resource<Unit> {
        val token = prefs.getString(
            PrefsKeys.JWT, null
        ) ?: return Resource.Error()

        return getResourceResult { api.authenticate("Bearer $token") }
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
