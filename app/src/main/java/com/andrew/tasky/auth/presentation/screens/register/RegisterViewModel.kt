package com.andrew.tasky.auth.presentation.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.auth.data.AuthRepository
import com.andrew.tasky.auth.data.AuthResult
import com.andrew.tasky.auth.domain.EmailPatternValidator
import com.andrew.tasky.auth.util.NameValidator
import com.andrew.tasky.auth.util.PasswordValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val emailPatternValidator: EmailPatternValidator
) : ViewModel() {
    private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()

    private val _isNameValid = MutableStateFlow(false)
    val isNameValid = _isNameValid.asStateFlow()
    fun setIsNameValid(name: String) {
        _isNameValid.value = NameValidator().validate(name)
    }

    fun isPasswordValid(password: String): Boolean {
        return PasswordValidator().validate(password)
    }

    fun register(fullName: String, email: String, password: String) {
        if (
            emailPatternValidator.isValidEmailPattern(email) &&
            isNameValid.value &&
            isPasswordValid(email)
        ) {
            viewModelScope.launch {
                val result = repository.register(
                    fullName = fullName,
                    email = email,
                    password = password
                )
                resultChannel.send(result)
            }
        }
    }
}
