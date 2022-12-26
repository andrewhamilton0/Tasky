package com.andrew.tasky.auth.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.auth.AuthRepository
import com.andrew.tasky.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    private val _isNameValid = MutableStateFlow(false)
    val isNameValid = _isNameValid.asStateFlow()
    fun setIsNameValid(nameValidity: Boolean) {
        _isNameValid.value = nameValidity
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.register(
                name = name,
                email = email,
                password = password
            )
            resultChannel.send(result)
        }
    }
}
