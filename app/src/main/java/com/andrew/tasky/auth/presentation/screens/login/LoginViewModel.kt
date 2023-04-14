package com.andrew.tasky.auth.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.auth.domain.AuthRepository
import com.andrew.tasky.core.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val loginResultChannel = Channel<Resource<Unit>>()
    val loginResults = loginResultChannel.receiveAsFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(
                email = username,
                password = password
            )
            loginResultChannel.send(result)
        }
    }
}
