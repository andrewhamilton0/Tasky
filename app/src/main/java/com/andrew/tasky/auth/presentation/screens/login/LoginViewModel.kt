package com.andrew.tasky.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.auth.data.AuthRepository
import com.andrew.tasky.auth.data.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    init {
        authenticate()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(
                email = username,
                password = password
            )
            resultChannel.send(result)
        }
    }

    private fun authenticate() {
        viewModelScope.launch {
            val result = repository.authenticate()
            resultChannel.send(result)
        }
    }
}
