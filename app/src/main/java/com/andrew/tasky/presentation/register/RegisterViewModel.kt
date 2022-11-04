package com.andrew.tasky.presentation.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {

    private val _isNameValid = MutableStateFlow(false)
    val isNameValid = _isNameValid.asStateFlow()
    fun setIsNameValid(nameValidity: Boolean) {
        _isNameValid.value = nameValidity
    }
}
