package com.andrew.tasky.core.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.tasky.core.domain.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isUserLoggedIn.value = sharedPrefs.containsJwt()
            _isLoading.value = false
        }
    }
}
