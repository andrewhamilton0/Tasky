package com.andrew.tasky.auth.data

import androidx.work.*
import com.andrew.tasky.agenda.domain.AgendaRepository
import com.andrew.tasky.auth.data.networkModels.LoginRequest
import com.andrew.tasky.auth.data.networkModels.RegisterRequest
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.auth.util.getResourceResult
import com.andrew.tasky.core.data.Resource
import com.andrew.tasky.core.data.util.ApiErrorType
import com.andrew.tasky.core.domain.SharedPrefs
import com.andrew.tasky.core.util.WorkerParamKeys

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: SharedPrefs,
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
                result.data?.token?.let { prefs.putJwt(it) }
                result.data?.userId?.let { prefs.putUserId(it) }
                result.data?.fullName?.let { prefs.putFullName(it) }
                Resource.Success()
            }
        }
    }

    override suspend fun authenticate(): Resource<Unit> {
        val token = prefs.getJwt() ?: return Resource.Error(errorType = ApiErrorType.Unauthorized)
        return getResourceResult { api.authenticate("Bearer $token") }
    }

    override suspend fun logout() {
        val token = prefs.getJwt()
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
        prefs.clearPrefs()
        agendaRepository.deleteAllAgendaItems()
    }

    override suspend fun isAuthorizedToLogin(): Boolean {
        val result = authenticate()
        return when (result) {
            is Resource.Error -> {
                when (result.errorType) {
                    is ApiErrorType.Unauthorized -> false
                    else -> true
                }
            }
            is Resource.Success -> true
        }
    }
}
